package com.edutrack.app.ui.student;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.edutrack.app.R;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.ActivityStudentMainBinding;
import com.edutrack.app.databinding.DialogChangePasswordBinding;
import com.edutrack.app.model.SubjectAttendanceSummary;
import com.edutrack.app.ui.LoginActivity;
import com.edutrack.app.util.Constants;
import com.edutrack.app.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class StudentMainActivity extends AppCompatActivity {

    private ActivityStudentMainBinding binding;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    checkAndTriggerDeficitNotification();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        databaseHelper = DatabaseHelper.getInstance(this);

        setupToolbar();
        setupBottomNavigation();
        setupNotificationChannel();
        requestNotificationPermissionIfNeeded();

        // Default screen: Dashboard
        if (savedInstanceState == null) {
            loadFragment(new StudentDashboardFragment());
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.studentToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            String name = sessionManager.getUserName();
            String roll = sessionManager.getRollNo();
            if (!name.isEmpty()) {
                getSupportActionBar().setSubtitle(String.format("%s (Roll: %s)", name, roll.isEmpty() ? "N/A" : roll));
            }
        }

        binding.studentToolbar.inflateMenu(R.menu.menu_student_toolbar);
        binding.studentToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                confirmLogout();
                return true;
            } else if (item.getItemId() == R.id.action_change_password) {
                showChangePasswordDialog();
                return true;
            }
            return false;
        });
    }

    private void showChangePasswordDialog() {
        DialogChangePasswordBinding dialogBinding = DialogChangePasswordBinding.inflate(getLayoutInflater());

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String newPass = dialogBinding.etNewPassword.getText() != null ? dialogBinding.etNewPassword.getText().toString().trim() : "";
                String confirmPass = dialogBinding.etConfirmPassword.getText() != null ? dialogBinding.etConfirmPassword.getText().toString().trim() : "";

                if (newPass.isEmpty()) {
                    dialogBinding.etNewPassword.setError("New password required");
                    return;
                }
                if (newPass.length() < 6) {
                    dialogBinding.etNewPassword.setError("Password must be at least 6 characters");
                    return;
                }
                if (!newPass.equals(confirmPass)) {
                    dialogBinding.etConfirmPassword.setError("Passwords do not match");
                    return;
                }

                long userId = sessionManager.getUserId();
                boolean success = databaseHelper.updatePassword(userId, newPass);
                if (success) {
                    Toast.makeText(this, "Password updated successfully. Please log in again.", Toast.LENGTH_LONG).show();
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void setupBottomNavigation() {
        binding.studentBottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_student_dashboard) {
                loadFragment(new StudentDashboardFragment());
                return true;
            } else if (id == R.id.nav_student_subjects) {
                loadFragment(new StudentSubjectsFragment());
                return true;
            } else if (id == R.id.nav_student_announcements) {
                loadFragment(new StudentAnnouncementsFragment());
                return true;
            } else if (id == R.id.nav_student_alerts) {
                loadFragment(new StudentAlertsFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.studentFragmentContainer, fragment)
                .commit();
    }

    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID,
                    Constants.NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alerts for attendance dropping below 75% minimum threshold");
            channel.enableVibration(true);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                checkAndTriggerDeficitNotification();
            }
        } else {
            checkAndTriggerDeficitNotification();
        }
    }

    private void checkAndTriggerDeficitNotification() {
        long studentId = sessionManager.getStudentId();
        if (studentId == -1) return;

        List<SubjectAttendanceSummary> summaries = databaseHelper.getAllSubjectSummariesForStudent(studentId);
        int belowCount = 0;
        String sampleDeficitSubject = "";
        double sampleDeficitPct = 0;

        for (SubjectAttendanceSummary s : summaries) {
            if (!s.isOnTrack()) {
                belowCount++;
                if (sampleDeficitSubject.isEmpty()) {
                    sampleDeficitSubject = s.getSubjectCode();
                    sampleDeficitPct = s.getPercentage();
                }
            }
        }

        if (belowCount > 0) {
            Intent intent = new Intent(this, StudentMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_warning)
                    .setContentTitle("Attendance Deficit Warning")
                    .setContentText(String.format(Locale.getDefault(),
                            "You have %d subject%s below 75%% (e.g. %s: %.1f%%). Attend classes to avoid detention.",
                            belowCount, belowCount == 1 ? "" : "s", sampleDeficitSubject, sampleDeficitPct))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(
                            String.format(Locale.getDefault(),
                                    "Attendance Warning: You have %d subject%s falling below the mandatory 75%% rule (including %s at %.1f%%). Tap to view required lectures.",
                                    belowCount, belowCount == 1 ? "" : "s", sampleDeficitSubject, sampleDeficitPct)))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(Constants.NOTIFICATION_ID_ALERT, builder.build());
            }
        }
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.action_confirm, (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
