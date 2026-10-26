package com.edutrack.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.edutrack.app.R;
import com.edutrack.app.adapter.AdminAnnouncementsAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.DialogChangePasswordBinding;
import com.edutrack.app.databinding.DialogPostAnnouncementBinding;
import com.edutrack.app.databinding.FragmentAdminAnnouncementsBinding;
import com.edutrack.app.model.Announcement;
import com.edutrack.app.ui.LoginActivity;
import com.edutrack.app.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminAnnouncementsFragment extends Fragment implements AdminAnnouncementsAdapter.OnAnnouncementActionListener {

    private FragmentAdminAnnouncementsBinding binding;
    private DatabaseHelper dbHelper;
    private AdminAnnouncementsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminAnnouncementsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());

        setupToolbarMenu();

        adapter = new AdminAnnouncementsAdapter(this);
        binding.rvAdminAnnouncements.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminAnnouncements.setAdapter(adapter);

        binding.fabAddAdminAnnouncement.setOnClickListener(v -> showPostAnnouncementDialog());

        loadAnnouncements();
    }

    private void setupToolbarMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_admin_toolbar, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_admin_logout) {
                    showLogoutDialog();
                    return true;
                } else if (menuItem.getItemId() == R.id.action_change_password) {
                    showChangePasswordDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void showChangePasswordDialog() {
        DialogChangePasswordBinding dialogBinding = DialogChangePasswordBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
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

                SessionManager sessionManager = new SessionManager(requireContext());
                long userId = sessionManager.getUserId();
                boolean success = DatabaseHelper.getInstance(requireContext()).updatePassword(userId, newPass);
                if (success) {
                    Toast.makeText(requireContext(), "Password updated successfully. Please log in again.", Toast.LENGTH_LONG).show();
                    sessionManager.logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void loadAnnouncements() {
        List<Announcement> announcements = dbHelper.getAllAnnouncements();
        adapter.setAnnouncements(announcements);
        binding.tvEmptyAdminAnnouncements.setVisibility(announcements.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showPostAnnouncementDialog() {
        DialogPostAnnouncementBinding dialogBinding = DialogPostAnnouncementBinding.inflate(getLayoutInflater());
        dialogBinding.etTargetClass.setText("SE Computer Engineering - Div A");

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Publish", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String title = dialogBinding.etNoticeTitle.getText() != null ? dialogBinding.etNoticeTitle.getText().toString().trim() : "";
                String message = dialogBinding.etNoticeMessage.getText() != null ? dialogBinding.etNoticeMessage.getText().toString().trim() : "";
                String className = dialogBinding.etTargetClass.getText() != null ? dialogBinding.etTargetClass.getText().toString().trim() : "";

                if (title.isEmpty()) {
                    dialogBinding.etNoticeTitle.setError("Title required");
                    return;
                }
                if (message.isEmpty()) {
                    dialogBinding.etNoticeMessage.setError("Message required");
                    return;
                }
                if (className.isEmpty()) {
                    dialogBinding.etTargetClass.setError("Target class required");
                    return;
                }

                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                long id = dbHelper.addAnnouncement("College Administration", className, title, message, currentDate);
                if (id != -1) {
                    Toast.makeText(requireContext(), "Notice broadcasted successfully!", Toast.LENGTH_SHORT).show();
                    loadAnnouncements();
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Failed to post notice.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of the administration console?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    SessionManager sessionManager = new SessionManager(requireContext());
                    sessionManager.logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDelete(Announcement announcement) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Announcement")
                .setMessage("Are you sure you want to delete this notice: \"" + announcement.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = dbHelper.deleteAnnouncement(announcement.getId());
                    if (success) {
                        Toast.makeText(requireContext(), "Notice deleted", Toast.LENGTH_SHORT).show();
                        loadAnnouncements();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete notice", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
