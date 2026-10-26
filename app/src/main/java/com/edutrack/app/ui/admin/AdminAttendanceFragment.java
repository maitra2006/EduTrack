package com.edutrack.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.edutrack.app.R;
import com.edutrack.app.adapter.AdminAttendanceAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.DialogAdminEditAttendanceBinding;
import com.edutrack.app.databinding.FragmentAdminAttendanceBinding;
import com.edutrack.app.model.AttendanceRecord;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class AdminAttendanceFragment extends Fragment implements AdminAttendanceAdapter.OnAttendanceActionListener {

    private FragmentAdminAttendanceBinding binding;
    private DatabaseHelper dbHelper;
    private AdminAttendanceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminAttendanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());

        adapter = new AdminAttendanceAdapter(this);
        binding.rvAdminAttendance.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminAttendance.setAdapter(adapter);

        loadAttendanceRecords();
    }

    private void loadAttendanceRecords() {
        List<AttendanceRecord> records = dbHelper.getAllAttendanceRecords();
        adapter.setRecords(records);
        binding.tvEmptyAdminAttendance.setVisibility(records.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(AttendanceRecord record) {
        DialogAdminEditAttendanceBinding editBinding = DialogAdminEditAttendanceBinding.inflate(getLayoutInflater());
        editBinding.tvEditAttStudentInfo.setText(record.getStudentName() + " (Roll: " + record.getRollNo() + ")");
        editBinding.tvEditAttDate.setText("Date: " + record.getDate());
        editBinding.switchEditAttendance.setChecked(record.isPresent());

        updateSwitchLabel(editBinding, record.isPresent());

        editBinding.switchEditAttendance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateSwitchLabel(editBinding, isChecked);
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setView(editBinding.getRoot())
                .setPositiveButton("Save Status", (dialog, which) -> {
                    int newStatus = editBinding.switchEditAttendance.isChecked() ? 1 : 0;
                    boolean success = dbHelper.updateAttendanceRecord(record.getId(), newStatus);
                    if (success) {
                        Toast.makeText(requireContext(), "Attendance status updated", Toast.LENGTH_SHORT).show();
                        loadAttendanceRecords();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update record", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateSwitchLabel(DialogAdminEditAttendanceBinding editBinding, boolean isPresent) {
        if (isPresent) {
            editBinding.tvEditAttStatusLabel.setText("Attendance Status: Present");
            editBinding.tvEditAttStatusLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_green));
        } else {
            editBinding.tvEditAttStatusLabel.setText("Attendance Status: Absent");
            editBinding.tvEditAttStatusLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_red));
        }
    }

    @Override
    public void onDelete(AttendanceRecord record) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Attendance Entry")
                .setMessage("Are you sure you want to delete attendance for " + record.getStudentName() + " on " + record.getDate() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = dbHelper.deleteAttendanceRecord(record.getId());
                    if (success) {
                        Toast.makeText(requireContext(), "Attendance record removed", Toast.LENGTH_SHORT).show();
                        loadAttendanceRecords();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete record", Toast.LENGTH_SHORT).show();
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
