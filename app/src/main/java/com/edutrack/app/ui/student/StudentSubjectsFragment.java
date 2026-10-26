package com.edutrack.app.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edutrack.app.adapter.StudentSubjectsAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.FragmentStudentSubjectsBinding;
import com.edutrack.app.model.Subject;
import com.edutrack.app.util.SessionManager;

import java.util.List;

public class StudentSubjectsFragment extends Fragment {

    private FragmentStudentSubjectsBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private StudentSubjectsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentSubjectsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        String className = sessionManager.getClassName();
        if (className != null && !className.isEmpty()) {
            binding.tvStudentClassName.setText("Class: " + className);
        }

        adapter = new StudentSubjectsAdapter();
        binding.rvSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSubjects.setAdapter(adapter);

        loadSubjects();
    }

    private void loadSubjects() {
        String className = sessionManager.getClassName();
        List<Subject> subjects = dbHelper.getSubjectsForClass(className != null ? className : "");

        if (subjects.isEmpty()) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.rvSubjects.setVisibility(View.GONE);
        } else {
            binding.tvEmptyState.setVisibility(View.GONE);
            binding.rvSubjects.setVisibility(View.VISIBLE);
            adapter.setList(subjects);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
