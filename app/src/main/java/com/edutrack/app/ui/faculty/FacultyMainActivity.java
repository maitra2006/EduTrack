package com.edutrack.app.ui.faculty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.edutrack.app.R;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.ActivityFacultyMainBinding;
import com.edutrack.app.databinding.DialogChangePasswordBinding;
import com.edutrack.app.ui.LoginActivity;
import com.edutrack.app.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class FacultyMainActivity extends AppCompatActivity {

    private ActivityFacultyMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultyMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        setupToolbar();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new FacultyClassesFragment());
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.facultyToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("EduTrack - Faculty");
            String name = sessionManager.getUserName();
            if (!name.isEmpty()) {
                getSupportActionBar().setSubtitle(name);
            }
        }

        binding.facultyToolbar.inflateMenu(R.menu.menu_faculty_toolbar);
        binding.facultyToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_faculty_logout) {
                confirmLogout();
                return true;
            } else if (item.getItemId() == R.id.action_change_password) {
                showChangePasswordDialog();
                return true;
            }
            return false;
        });
    }

    private void setupBottomNavigation() {
        binding.facultyBottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_faculty_classes) {
                loadFragment(new FacultyClassesFragment());
                return true;
            } else if (id == R.id.nav_faculty_attendance) {
                loadFragment(new FacultyMarkAttendanceFragment());
                return true;
            } else if (id == R.id.nav_faculty_announcements) {
                loadFragment(new FacultyAnnouncementsFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.facultyFragmentContainer, fragment)
                .commit();
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
                boolean success = DatabaseHelper.getInstance(this).updatePassword(userId, newPass);
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
