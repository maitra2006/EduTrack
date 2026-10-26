package com.edutrack.app.ui.faculty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edutrack.app.adapter.FacultyClassesAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.FragmentFacultyClassesBinding;
import com.edutrack.app.model.Subject;
import com.edutrack.app.util.SessionManager;

import java.util.List;

public class FacultyClassesFragment extends Fragment {

    private FragmentFacultyClassesBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private FacultyClassesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFacultyClassesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        String facultyName = sessionManager.getUserName();
        if (!facultyName.isEmpty()) {
            binding.tvFacultyGreeting.setText(facultyName + "'s Teaching Assignments");
        }

        adapter = new FacultyClassesAdapter();
        binding.rvFacultyClasses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFacultyClasses.setAdapter(adapter);

        loadAssignedClasses();
    }

    private void loadAssignedClasses() {
        long facultyId = sessionManager.getFacultyId();
        List<Subject> subjects = dbHelper.getSubjectsForFaculty(facultyId);

        if (subjects.isEmpty()) {
            binding.tvEmptyClasses.setVisibility(View.VISIBLE);
            binding.rvFacultyClasses.setVisibility(View.GONE);
        } else {
            binding.tvEmptyClasses.setVisibility(View.GONE);
            binding.rvFacultyClasses.setVisibility(View.VISIBLE);
            adapter.setList(subjects);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
