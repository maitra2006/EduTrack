package com.edutrack.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.edutrack.app.R;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.ActivityAdminMainBinding;
import com.edutrack.app.databinding.DialogChangePasswordBinding;
import com.edutrack.app.ui.LoginActivity;
import com.edutrack.app.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        setSupportActionBar(binding.adminToolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFragmentContainer, new AdminDashboardFragment())
                    .commit();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateToolbarState);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });

        binding.adminToolbar.setNavigationOnClickListener(v -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void updateToolbarState() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount > 0) {
            binding.adminToolbar.setNavigationIcon(R.drawable.ic_expand_less); // Or back indicator
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.adminFragmentContainer);
            if (currentFragment instanceof AdminStudentsFragment) {
                binding.adminToolbar.setTitle("Manage Students");
            } else if (currentFragment instanceof AdminFacultyFragment) {
                binding.adminToolbar.setTitle("Manage Faculty");
            } else if (currentFragment instanceof AdminSubjectsFragment) {
                binding.adminToolbar.setTitle("Manage Subjects");
            } else if (currentFragment instanceof AdminAttendanceFragment) {
                binding.adminToolbar.setTitle("Attendance History");
            } else if (currentFragment instanceof AdminAnnouncementsFragment) {
                binding.adminToolbar.setTitle("Campus Notices");
            }
        } else {
            binding.adminToolbar.setNavigationIcon(null);
            binding.adminToolbar.setTitle("EduTrack - Administration");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_admin_logout) {
            showLogoutDialog();
            return true;
        } else if (item.getItemId() == R.id.action_change_password) {
            showChangePasswordDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of the administration console?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
