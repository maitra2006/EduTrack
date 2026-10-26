package com.edutrack.app.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edutrack.app.adapter.StudentAlertsAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.FragmentStudentAlertsBinding;
import com.edutrack.app.model.SubjectAttendanceSummary;
import com.edutrack.app.util.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentAlertsFragment extends Fragment {

    private FragmentStudentAlertsBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private StudentAlertsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentAlertsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        adapter = new StudentAlertsAdapter();
        binding.rvAlerts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAlerts.setAdapter(adapter);

        loadAlerts();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAlerts();
    }

    private void loadAlerts() {
        long studentId = sessionManager.getStudentId();
        if (studentId == -1) {
            return;
        }

        List<SubjectAttendanceSummary> summaries = dbHelper.getAllSubjectSummariesForStudent(studentId);
        List<SubjectAttendanceSummary> alertList = new ArrayList<>();

        // Add subjects that have warnings or deficits first
        for (SubjectAttendanceSummary s : summaries) {
            if (!s.isOnTrack()) {
                alertList.add(s);
            }
        }

        // Sort deficits by lowest percentage first
        Collections.sort(alertList, (a, b) -> Double.compare(a.getPercentage(), b.getPercentage()));

        if (alertList.isEmpty()) {
            binding.layoutEmptyAlerts.setVisibility(View.VISIBLE);
            binding.rvAlerts.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyAlerts.setVisibility(View.GONE);
            binding.rvAlerts.setVisibility(View.VISIBLE);
            adapter.setList(alertList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
