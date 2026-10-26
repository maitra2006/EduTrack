package com.edutrack.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.edutrack.app.adapter.AdminSubjectsAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.DialogAdminSubjectBinding;
import com.edutrack.app.databinding.FragmentAdminSubjectsBinding;
import com.edutrack.app.model.Subject;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class AdminSubjectsFragment extends Fragment implements AdminSubjectsAdapter.OnSubjectActionListener {

    private FragmentAdminSubjectsBinding binding;
    private DatabaseHelper dbHelper;
    private AdminSubjectsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminSubjectsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());

        adapter = new AdminSubjectsAdapter(this);
        binding.rvAdminSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminSubjects.setAdapter(adapter);

        binding.fabAddSubject.setOnClickListener(v -> showSubjectDialog(null));

        loadSubjects();
    }

    private void loadSubjects() {
        List<Subject> subjects = dbHelper.getAllSubjects();
        adapter.setSubjects(subjects);
        binding.tvEmptyAdminSubjects.setVisibility(subjects.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showSubjectDialog(@Nullable Subject existingSubject) {
        DialogAdminSubjectBinding dialogBinding = DialogAdminSubjectBinding.inflate(getLayoutInflater());
        boolean isEdit = existingSubject != null;

        dialogBinding.tvSubjectDialogTitle.setText(isEdit ? "Edit Subject Details" : "Create New Subject");
        if (isEdit) {
            dialogBinding.etSubjectCode.setText(existingSubject.getCode());
            dialogBinding.etSubjectName.setText(existingSubject.getName());
            dialogBinding.etSubjectDept.setText(existingSubject.getDepartment());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton(isEdit ? "Update" : "Create", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String code = dialogBinding.etSubjectCode.getText() != null ? dialogBinding.etSubjectCode.getText().toString().trim().toUpperCase() : "";
                String name = dialogBinding.etSubjectName.getText() != null ? dialogBinding.etSubjectName.getText().toString().trim() : "";
                String dept = dialogBinding.etSubjectDept.getText() != null ? dialogBinding.etSubjectDept.getText().toString().trim() : "";

                if (code.isEmpty()) {
                    dialogBinding.etSubjectCode.setError("Subject code required");
                    return;
                }
                if (name.isEmpty()) {
                    dialogBinding.etSubjectName.setError("Subject name required");
                    return;
                }
                if (dept.isEmpty()) {
                    dialogBinding.etSubjectDept.setError("Department required");
                    return;
                }

                if (isEdit) {
                    boolean success = dbHelper.updateSubject(existingSubject.getId(), code, name, dept);
                    if (success) {
                        Toast.makeText(requireContext(), "Subject updated successfully", Toast.LENGTH_SHORT).show();
                        loadSubjects();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update subject", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    long id = dbHelper.addSubject(code, name, dept);
                    if (id != -1) {
                        Toast.makeText(requireContext(), "Subject added successfully", Toast.LENGTH_SHORT).show();
                        loadSubjects();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Failed to add subject (code may already exist)", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onEdit(Subject subject) {
        showSubjectDialog(subject);
    }

    @Override
    public void onDelete(Subject subject) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Subject")
                .setMessage("Are you sure you want to delete " + subject.getCode() + " - " + subject.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = dbHelper.deleteSubject(subject.getId());
                    if (success) {
                        Toast.makeText(requireContext(), "Subject deleted", Toast.LENGTH_SHORT).show();
                        loadSubjects();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete subject", Toast.LENGTH_SHORT).show();
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
