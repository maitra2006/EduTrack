package com.edutrack.app.ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.edutrack.app.R;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.DialogAdminAssignBinding;
import com.edutrack.app.databinding.FragmentAdminDashboardBinding;
import com.edutrack.app.model.Faculty;
import com.edutrack.app.model.Student;
import com.edutrack.app.model.Subject;
import com.edutrack.app.model.SubjectAttendanceSummary;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());

        loadStatistics();
        setupPieChart();
        setupNavigation();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
        setupPieChart();
    }

    private void loadStatistics() {
        int totalStudents = dbHelper.getTotalStudentsCount();
        int totalFaculty = dbHelper.getTotalFacultyCount();
        int totalSubjects = dbHelper.getTotalSubjectsCount();
        double avgAttendance = dbHelper.getCollegeAverageAttendance();

        binding.tvStatStudents.setText(String.valueOf(totalStudents));
        binding.tvStatFaculty.setText(String.valueOf(totalFaculty));
        binding.tvStatSubjects.setText(String.valueOf(totalSubjects));
        binding.tvStatAvgAttendance.setText(String.format(Locale.getDefault(), "%.1f%%", avgAttendance));
    }

    private void setupPieChart() {
        List<Student> students = dbHelper.getAllStudents();
        int safeCount = 0;
        int warningCount = 0;

        for (Student s : students) {
            SubjectAttendanceSummary summary = dbHelper.getOverallAttendanceForStudent(s.getStudentId());
            if (summary.getPercentage() >= 75.0) {
                safeCount++;
            } else {
                warningCount++;
            }
        }

        if (safeCount == 0 && warningCount == 0) {
            safeCount = 1; // Default fallback for preview
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (safeCount > 0) {
            entries.add(new PieEntry(safeCount, "Safe (>=75%)"));
            colors.add(ContextCompat.getColor(requireContext(), R.color.status_green));
        }
        if (warningCount > 0) {
            entries.add(new PieEntry(warningCount, "Low (<75%)"));
            colors.add(ContextCompat.getColor(requireContext(), R.color.status_red));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.pieChartAdmin));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        binding.pieChartAdmin.setUsePercentValues(true);
        binding.pieChartAdmin.getDescription().setEnabled(false);
        binding.pieChartAdmin.getLegend().setEnabled(true);
        binding.pieChartAdmin.setDrawHoleEnabled(true);
        binding.pieChartAdmin.setHoleColor(Color.TRANSPARENT);
        binding.pieChartAdmin.setTransparentCircleRadius(58f);
        binding.pieChartAdmin.setHoleRadius(52f);
        binding.pieChartAdmin.setCenterText("Eligibility");
        binding.pieChartAdmin.setCenterTextSize(14f);
        binding.pieChartAdmin.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        binding.pieChartAdmin.setData(data);
        binding.pieChartAdmin.animateY(1000, Easing.EaseInOutQuad);
        binding.pieChartAdmin.invalidate();
    }

    private void setupNavigation() {
        binding.cardNavStudents.setOnClickListener(v -> navigateTo(new AdminStudentsFragment()));
        binding.cardNavFaculty.setOnClickListener(v -> navigateTo(new AdminFacultyFragment()));
        binding.cardNavSubjects.setOnClickListener(v -> navigateTo(new AdminSubjectsFragment()));
        binding.cardNavAssignments.setOnClickListener(v -> showAssignmentDialog());
        binding.cardNavAttendance.setOnClickListener(v -> navigateTo(new AdminAttendanceFragment()));
        binding.cardNavAnnouncements.setOnClickListener(v -> navigateTo(new AdminAnnouncementsFragment()));
    }

    private void showAssignmentDialog() {
        DialogAdminAssignBinding assignBinding = DialogAdminAssignBinding.inflate(getLayoutInflater());
        List<Faculty> facultyList = dbHelper.getAllFaculty();
        List<Subject> subjectList = dbHelper.getAllSubjects();

        if (facultyList.isEmpty() || subjectList.isEmpty()) {
            Toast.makeText(requireContext(), "Please ensure both Faculty and Subjects exist before creating assignments.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> facultyNames = new ArrayList<>();
        for (Faculty f : facultyList) {
            facultyNames.add(f.getName() + " (" + f.getDepartment() + ")");
        }

        List<String> subjectNames = new ArrayList<>();
        for (Subject s : subjectList) {
            subjectNames.add(s.getCode() + " - " + s.getName());
        }

        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, facultyNames);
        assignBinding.spinnerAssignFaculty.setAdapter(facultyAdapter);

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, subjectNames);
        assignBinding.spinnerAssignSubject.setAdapter(subjectAdapter);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(assignBinding.getRoot())
                .setPositiveButton("Assign", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                int selectedFacultyIndex = assignBinding.spinnerAssignFaculty.getSelectedItemPosition();
                int selectedSubjectIndex = assignBinding.spinnerAssignSubject.getSelectedItemPosition();
                String className = assignBinding.etAssignClass.getText() != null ?
                        assignBinding.etAssignClass.getText().toString().trim() : "";

                if (className.isEmpty()) {
                    assignBinding.etAssignClass.setError("Class name required");
                    return;
                }

                Faculty selectedFaculty = facultyList.get(selectedFacultyIndex);
                Subject selectedSubject = subjectList.get(selectedSubjectIndex);

                long result = dbHelper.assignFacultySubject(selectedFaculty.getFacultyId(), selectedSubject.getId(), className);
                if (result != -1) {
                    Toast.makeText(requireContext(), "Assignment created successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Failed to create assignment.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void navigateTo(Fragment fragment) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.adminFragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
