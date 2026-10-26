package com.edutrack.app.ui.student;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edutrack.app.R;
import com.edutrack.app.adapter.SubjectAttendanceAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.FragmentStudentDashboardBinding;
import com.edutrack.app.model.SubjectAttendanceSummary;
import com.edutrack.app.util.Constants;
import com.edutrack.app.util.SessionManager;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentDashboardFragment extends Fragment {

    private FragmentStudentDashboardBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private SubjectAttendanceAdapter attendanceAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        setupRecyclerView();
        loadDashboardData();
    }

    private void setupRecyclerView() {
        attendanceAdapter = new SubjectAttendanceAdapter();
        binding.rvSubjectAttendance.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSubjectAttendance.setAdapter(attendanceAdapter);
    }

    private void loadDashboardData() {
        long studentId = sessionManager.getStudentId();
        if (studentId == -1) {
            return;
        }

        // 1. Overall Attendance Summary
        SubjectAttendanceSummary overall = dbHelper.getOverallAttendanceForStudent(studentId);
        List<SubjectAttendanceSummary> summaries = dbHelper.getAllSubjectSummariesForStudent(studentId);

        // Update overall card texts
        binding.tvOverallAttendanceStats.setText(String.format(Locale.getDefault(),
                "%d of %d Lectures Attended", overall.getAttendedLectures(), overall.getTotalLectures()));

        updateOverallStatusBadge(overall.getPercentage());

        // Setup Donut PieChart
        setupDonutChart(overall);

        // Check for subjects below minimum (75%)
        int belowCount = 0;
        for (SubjectAttendanceSummary s : summaries) {
            if (!s.isOnTrack()) {
                belowCount++;
            }
        }

        if (belowCount > 0) {
            binding.cardWarningBanner.setVisibility(View.VISIBLE);
            binding.tvWarningSubtitle.setText(String.format(Locale.getDefault(),
                    "%d subject%s below the mandatory %.0f%% attendance threshold.",
                    belowCount, belowCount == 1 ? " is" : "s are", Constants.MIN_ATTENDANCE));
        } else {
            binding.cardWarningBanner.setVisibility(View.GONE);
        }

        // Setup BarChart
        setupBarChart(summaries);

        // Update RecyclerView list
        if (summaries.isEmpty()) {
            binding.tvEmptySubjects.setVisibility(View.VISIBLE);
            binding.rvSubjectAttendance.setVisibility(View.GONE);
        } else {
            binding.tvEmptySubjects.setVisibility(View.GONE);
            binding.rvSubjectAttendance.setVisibility(View.VISIBLE);
            attendanceAdapter.setList(summaries);
        }
    }

    private void updateOverallStatusBadge(double percentage) {
        int colorGreen = ContextCompat.getColor(requireContext(), R.color.status_green);
        int colorGreenBg = ContextCompat.getColor(requireContext(), R.color.status_green_bg);
        int colorAmber = ContextCompat.getColor(requireContext(), R.color.status_amber);
        int colorAmberBg = ContextCompat.getColor(requireContext(), R.color.status_amber_bg);
        int colorRed = ContextCompat.getColor(requireContext(), R.color.status_red);
        int colorRedBg = ContextCompat.getColor(requireContext(), R.color.status_red_bg);

        if (percentage >= Constants.MIN_ATTENDANCE) {
            binding.tvOverallStatusBadge.setText(String.format(Locale.getDefault(), "Status: Compliant (%.1f%% ≥ 75%%)", percentage));
            binding.tvOverallStatusBadge.setTextColor(colorGreen);
            binding.tvOverallStatusBadge.setBackgroundTintList(ColorStateList.valueOf(colorGreenBg));
        } else if (percentage >= 65.0) {
            binding.tvOverallStatusBadge.setText(String.format(Locale.getDefault(), "Status: Warning (%.1f%% < 75%%)", percentage));
            binding.tvOverallStatusBadge.setTextColor(colorAmber);
            binding.tvOverallStatusBadge.setBackgroundTintList(ColorStateList.valueOf(colorAmberBg));
        } else {
            binding.tvOverallStatusBadge.setText(String.format(Locale.getDefault(), "Status: Critical Deficit (%.1f%%)", percentage));
            binding.tvOverallStatusBadge.setTextColor(colorRed);
            binding.tvOverallStatusBadge.setBackgroundTintList(ColorStateList.valueOf(colorRedBg));
        }
    }

    private void setupDonutChart(SubjectAttendanceSummary overall) {
        binding.pieChartOverall.getDescription().setEnabled(false);
        binding.pieChartOverall.getLegend().setEnabled(false);
        binding.pieChartOverall.setDrawEntryLabels(false);
        binding.pieChartOverall.setDrawHoleEnabled(true);
        binding.pieChartOverall.setHoleColor(Color.TRANSPARENT);
        binding.pieChartOverall.setHoleRadius(72f);
        binding.pieChartOverall.setTransparentCircleRadius(76f);
        binding.pieChartOverall.setTransparentCircleColor(Color.TRANSPARENT);

        double pct = overall.getPercentage();
        int colorPrimary = ContextCompat.getColor(requireContext(), R.color.primary);
        binding.pieChartOverall.setCenterText(String.format(Locale.getDefault(), "%.1f%%\nOverall", pct));
        binding.pieChartOverall.setCenterTextSize(18f);
        binding.pieChartOverall.setCenterTextColor(colorPrimary);

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int attended = overall.getAttendedLectures();
        int missed = Math.max(0, overall.getTotalLectures() - attended);

        entries.add(new PieEntry(attended, "Attended"));
        entries.add(new PieEntry(missed, "Missed"));

        // Status color for attended slice
        if (pct >= Constants.MIN_ATTENDANCE) {
            colors.add(ContextCompat.getColor(requireContext(), R.color.status_green));
        } else if (pct >= 65.0) {
            colors.add(ContextCompat.getColor(requireContext(), R.color.status_amber));
        } else {
            colors.add(ContextCompat.getColor(requireContext(), R.color.status_red));
        }
        // Neutral gray for missed slice
        colors.add(ContextCompat.getColor(requireContext(), R.color.card_stroke));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        binding.pieChartOverall.setData(data);
        binding.pieChartOverall.invalidate();
    }

    private void setupBarChart(List<SubjectAttendanceSummary> summaries) {
        binding.barChartSubjects.getDescription().setEnabled(false);
        binding.barChartSubjects.getLegend().setEnabled(false);
        binding.barChartSubjects.setDrawGridBackground(false);
        binding.barChartSubjects.setDrawBarShadow(false);

        XAxis xAxis = binding.barChartSubjects.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));

        binding.barChartSubjects.getAxisRight().setEnabled(false);
        binding.barChartSubjects.getAxisLeft().setAxisMinimum(0f);
        binding.barChartSubjects.getAxisLeft().setAxisMaximum(100f);
        binding.barChartSubjects.getAxisLeft().setDrawGridLines(true);
        binding.barChartSubjects.getAxisLeft().setGridColor(ContextCompat.getColor(requireContext(), R.color.card_stroke));
        binding.barChartSubjects.getAxisLeft().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

        // Add 75% Limit Line
        binding.barChartSubjects.getAxisLeft().removeAllLimitLines();
        LimitLine minLine = new LimitLine((float) Constants.MIN_ATTENDANCE, "75% Target");
        minLine.setLineColor(ContextCompat.getColor(requireContext(), R.color.status_red));
        minLine.setLineWidth(1.5f);
        minLine.enableDashedLine(10f, 8f, 0f);
        minLine.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_red));
        minLine.setTextSize(10f);
        binding.barChartSubjects.getAxisLeft().addLimitLine(minLine);

        List<BarEntry> barEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> barColors = new ArrayList<>();

        for (int i = 0; i < summaries.size(); i++) {
            SubjectAttendanceSummary s = summaries.get(i);
            barEntries.add(new BarEntry(i, (float) s.getPercentage()));
            labels.add(s.getSubjectCode());

            if (s.isOnTrack()) {
                barColors.add(ContextCompat.getColor(requireContext(), R.color.status_green));
            } else if (s.isWarning()) {
                barColors.add(ContextCompat.getColor(requireContext(), R.color.status_amber));
            } else {
                barColors.add(ContextCompat.getColor(requireContext(), R.color.status_red));
            }
        }

        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size());

        BarDataSet barDataSet = new BarDataSet(barEntries, "Attendance %");
        barDataSet.setColors(barColors);
        barDataSet.setValueTextSize(11f);
        barDataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0f%%", value);
            }
        });

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        binding.barChartSubjects.setData(barData);
        binding.barChartSubjects.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
