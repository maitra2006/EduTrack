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
import com.edutrack.app.adapter.AdminFacultyAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.DialogAdminFacultyBinding;
import com.edutrack.app.databinding.FragmentAdminFacultyBinding;
import com.edutrack.app.model.Faculty;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class AdminFacultyFragment extends Fragment implements AdminFacultyAdapter.OnFacultyActionListener {

    private FragmentAdminFacultyBinding binding;
    private DatabaseHelper dbHelper;
    private AdminFacultyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminFacultyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());

        adapter = new AdminFacultyAdapter(this);
        binding.rvAdminFaculty.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminFaculty.setAdapter(adapter);

        binding.fabAddFaculty.setOnClickListener(v -> showFacultyDialog(null));

        loadFaculty();
    }

    private void loadFaculty() {
        List<Faculty> facultyList = dbHelper.getAllFaculty();
        adapter.setFacultyList(facultyList);
        binding.tvEmptyAdminFaculty.setVisibility(facultyList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showFacultyDialog(@Nullable Faculty existingFaculty) {
        DialogAdminFacultyBinding dialogBinding = DialogAdminFacultyBinding.inflate(getLayoutInflater());
        boolean isEdit = existingFaculty != null;

        dialogBinding.tvFacultyDialogTitle.setText(isEdit ? "Edit Faculty Details" : "Register Faculty Member");
        if (isEdit) {
            dialogBinding.etFacultyName.setText(existingFaculty.getName());
            dialogBinding.etFacultyEmail.setText(existingFaculty.getEmail());
            dialogBinding.tilFacultyPassword.setVisibility(View.GONE);
            dialogBinding.etFacultyDept.setText(existingFaculty.getDepartment());
            dialogBinding.etFacultyDesignation.setText(existingFaculty.getDesignation());
            dialogBinding.etFacultyPhone.setText(existingFaculty.getPhone());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton(isEdit ? "Update" : "Create", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = dialogBinding.etFacultyName.getText() != null ? dialogBinding.etFacultyName.getText().toString().trim() : "";
                String email = dialogBinding.etFacultyEmail.getText() != null ? dialogBinding.etFacultyEmail.getText().toString().trim() : "";
                String dept = dialogBinding.etFacultyDept.getText() != null ? dialogBinding.etFacultyDept.getText().toString().trim() : "";
                String designation = dialogBinding.etFacultyDesignation.getText() != null ? dialogBinding.etFacultyDesignation.getText().toString().trim() : "";
                String phone = dialogBinding.etFacultyPhone.getText() != null ? dialogBinding.etFacultyPhone.getText().toString().trim() : "";
                String password = dialogBinding.etFacultyPassword.getText() != null ? dialogBinding.etFacultyPassword.getText().toString().trim() : "";

                if (name.isEmpty()) {
                    dialogBinding.etFacultyName.setError("Name required");
                    return;
                }
                if (email.isEmpty()) {
                    dialogBinding.etFacultyEmail.setError("Email required");
                    return;
                }
                if (!isEdit && password.isEmpty()) {
                    dialogBinding.etFacultyPassword.setError("Password required");
                    return;
                }
                if (dept.isEmpty()) {
                    dialogBinding.etFacultyDept.setError("Department required");
                    return;
                }
                if (designation.isEmpty()) {
                    dialogBinding.etFacultyDesignation.setError("Designation required");
                    return;
                }

                if (isEdit) {
                    boolean success = dbHelper.updateFaculty(
                            existingFaculty.getFacultyId(),
                            existingFaculty.getUserId(),
                            name,
                            email,
                            phone,
                            dept,
                            designation
                    );
                    if (success) {
                        Toast.makeText(requireContext(), "Faculty profile updated successfully", Toast.LENGTH_SHORT).show();
                        loadFaculty();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update faculty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    long id = dbHelper.addFaculty(name, email, password, phone, dept, designation);
                    if (id != -1) {
                        Toast.makeText(requireContext(), "Faculty member added successfully", Toast.LENGTH_SHORT).show();
                        loadFaculty();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Failed to add faculty (email may already exist)", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onEdit(Faculty faculty) {
        showFacultyDialog(faculty);
    }

    @Override
    public void onDelete(Faculty faculty) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Faculty Member")
                .setMessage("Are you sure you want to delete " + faculty.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = dbHelper.deleteFaculty(faculty.getFacultyId(), faculty.getUserId());
                    if (success) {
                        Toast.makeText(requireContext(), "Faculty member deleted", Toast.LENGTH_SHORT).show();
                        loadFaculty();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete faculty", Toast.LENGTH_SHORT).show();
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
