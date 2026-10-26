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
import com.edutrack.app.adapter.AdminStudentsAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.DialogAdminStudentBinding;
import com.edutrack.app.databinding.FragmentAdminStudentsBinding;
import com.edutrack.app.model.Student;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class AdminStudentsFragment extends Fragment implements AdminStudentsAdapter.OnStudentActionListener {

    private FragmentAdminStudentsBinding binding;
    private DatabaseHelper dbHelper;
    private AdminStudentsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminStudentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());

        adapter = new AdminStudentsAdapter(this);
        binding.rvAdminStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminStudents.setAdapter(adapter);

        binding.fabAddStudent.setOnClickListener(v -> showStudentDialog(null));

        loadStudents();
    }

    private void loadStudents() {
        List<Student> students = dbHelper.getAllStudents();
        adapter.setStudents(students);
        binding.tvEmptyAdminStudents.setVisibility(students.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showStudentDialog(@Nullable Student existingStudent) {
        DialogAdminStudentBinding dialogBinding = DialogAdminStudentBinding.inflate(getLayoutInflater());
        boolean isEdit = existingStudent != null;

        dialogBinding.tvStudentDialogTitle.setText(isEdit ? "Edit Student Details" : "Register New Student");
        if (isEdit) {
            dialogBinding.etStudentName.setText(existingStudent.getName());
            dialogBinding.etStudentEmail.setText(existingStudent.getEmail());
            dialogBinding.tilStudentPassword.setVisibility(View.GONE);
            dialogBinding.etStudentRollNo.setText(existingStudent.getRollNo());
            dialogBinding.etStudentClass.setText(existingStudent.getClassName());
            dialogBinding.etStudentPhone.setText(existingStudent.getPhone());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton(isEdit ? "Update" : "Create", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = dialogBinding.etStudentName.getText() != null ? dialogBinding.etStudentName.getText().toString().trim() : "";
                String email = dialogBinding.etStudentEmail.getText() != null ? dialogBinding.etStudentEmail.getText().toString().trim() : "";
                String rollNo = dialogBinding.etStudentRollNo.getText() != null ? dialogBinding.etStudentRollNo.getText().toString().trim() : "";
                String className = dialogBinding.etStudentClass.getText() != null ? dialogBinding.etStudentClass.getText().toString().trim() : "";
                String phone = dialogBinding.etStudentPhone.getText() != null ? dialogBinding.etStudentPhone.getText().toString().trim() : "";
                String password = dialogBinding.etStudentPassword.getText() != null ? dialogBinding.etStudentPassword.getText().toString().trim() : "";

                if (name.isEmpty()) {
                    dialogBinding.etStudentName.setError("Name required");
                    return;
                }
                if (email.isEmpty()) {
                    dialogBinding.etStudentEmail.setError("Email required");
                    return;
                }
                if (!isEdit && password.isEmpty()) {
                    dialogBinding.etStudentPassword.setError("Password required for new account");
                    return;
                }
                if (rollNo.isEmpty()) {
                    dialogBinding.etStudentRollNo.setError("Roll number required");
                    return;
                }
                if (className.isEmpty()) {
                    dialogBinding.etStudentClass.setError("Class required");
                    return;
                }

                if (isEdit) {
                    boolean success = dbHelper.updateStudent(
                            existingStudent.getStudentId(),
                            existingStudent.getUserId(),
                            name,
                            email,
                            phone,
                            rollNo,
                            className
                    );
                    if (success) {
                        Toast.makeText(requireContext(), "Student profile updated successfully", Toast.LENGTH_SHORT).show();
                        loadStudents();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update student profile", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    long id = dbHelper.addStudent(name, email, password, phone, rollNo, className);
                    if (id != -1) {
                        Toast.makeText(requireContext(), "Student registered successfully", Toast.LENGTH_SHORT).show();
                        loadStudents();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Failed to register student (email may already exist)", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onEdit(Student student) {
        showStudentDialog(student);
    }

    @Override
    public void onDelete(Student student) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + student.getName() + " (" + student.getRollNo() + ")? This will remove all their attendance records.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = dbHelper.deleteStudent(student.getStudentId(), student.getUserId());
                    if (success) {
                        Toast.makeText(requireContext(), "Student deleted successfully", Toast.LENGTH_SHORT).show();
                        loadStudents();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete student", Toast.LENGTH_SHORT).show();
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
