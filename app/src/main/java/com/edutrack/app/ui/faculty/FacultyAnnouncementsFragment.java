package com.edutrack.app.ui.faculty;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edutrack.app.adapter.FacultyAnnouncementsAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.DialogPostAnnouncementBinding;
import com.edutrack.app.databinding.FragmentFacultyAnnouncementsBinding;
import com.edutrack.app.model.Announcement;
import com.edutrack.app.util.Constants;
import com.edutrack.app.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FacultyAnnouncementsFragment extends Fragment {

    private FragmentFacultyAnnouncementsBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private FacultyAnnouncementsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFacultyAnnouncementsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        adapter = new FacultyAnnouncementsAdapter();
        binding.rvFacultyAnnouncements.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFacultyAnnouncements.setAdapter(adapter);

        binding.fabPostAnnouncement.setOnClickListener(v -> showPostAnnouncementDialog());

        loadAnnouncements();
    }

    private void loadAnnouncements() {
        List<Announcement> list = dbHelper.getAllAnnouncements();

        if (list.isEmpty()) {
            binding.tvEmptyFacultyAnnouncements.setVisibility(View.VISIBLE);
            binding.rvFacultyAnnouncements.setVisibility(View.GONE);
        } else {
            binding.tvEmptyFacultyAnnouncements.setVisibility(View.GONE);
            binding.rvFacultyAnnouncements.setVisibility(View.VISIBLE);
            adapter.setList(list);
        }
    }

    private void showPostAnnouncementDialog() {
        DialogPostAnnouncementBinding dialogBinding = DialogPostAnnouncementBinding.inflate(getLayoutInflater());
        dialogBinding.etTargetClass.setText(Constants.DEMO_CLASS_NAME);

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Publish", (dialog, which) -> {
                    String targetClass = dialogBinding.etTargetClass.getText() != null
                            ? dialogBinding.etTargetClass.getText().toString().trim() : Constants.DEMO_CLASS_NAME;
                    String title = dialogBinding.etNoticeTitle.getText() != null
                            ? dialogBinding.etNoticeTitle.getText().toString().trim() : "";
                    String message = dialogBinding.etNoticeMessage.getText() != null
                            ? dialogBinding.etNoticeMessage.getText().toString().trim() : "";

                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
                        Toast.makeText(requireContext(), "Title and message are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String facultyName = sessionManager.getUserName();
                    if (facultyName.isEmpty()) facultyName = "Faculty";

                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

                    long result = dbHelper.addAnnouncement(facultyName, targetClass, title, message, timestamp);
                    if (result != -1) {
                        Snackbar.make(binding.getRoot(), "Announcement published to " + targetClass, Snackbar.LENGTH_LONG).show();
                        loadAnnouncements();
                    } else {
                        Toast.makeText(requireContext(), "Failed to post notice", Toast.LENGTH_SHORT).show();
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
