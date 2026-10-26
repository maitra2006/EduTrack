package com.edutrack.app.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edutrack.app.adapter.StudentAnnouncementsAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.FragmentStudentAnnouncementsBinding;
import com.edutrack.app.model.Announcement;
import com.edutrack.app.util.SessionManager;

import java.util.List;

public class StudentAnnouncementsFragment extends Fragment {

    private FragmentStudentAnnouncementsBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private StudentAnnouncementsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentAnnouncementsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        adapter = new StudentAnnouncementsAdapter();
        binding.rvAnnouncements.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAnnouncements.setAdapter(adapter);

        loadAnnouncements();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        String className = sessionManager.getClassName();
        List<Announcement> announcements = dbHelper.getAnnouncementsForClass(className != null ? className : "");

        if (announcements.isEmpty()) {
            binding.tvEmptyAnnouncements.setVisibility(View.VISIBLE);
            binding.rvAnnouncements.setVisibility(View.GONE);
        } else {
            binding.tvEmptyAnnouncements.setVisibility(View.GONE);
            binding.rvAnnouncements.setVisibility(View.VISIBLE);
            adapter.setList(announcements);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
