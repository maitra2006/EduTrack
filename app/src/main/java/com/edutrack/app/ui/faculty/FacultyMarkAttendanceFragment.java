package com.edutrack.app.ui.faculty;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edutrack.app.R;
import com.edutrack.app.adapter.FacultyAttendanceStudentAdapter;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.FragmentFacultyMarkAttendanceBinding;
import com.edutrack.app.model.AttendanceRecord;
import com.edutrack.app.model.Subject;
import com.edutrack.app.util.Constants;
import com.edutrack.app.util.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FacultyMarkAttendanceFragment extends Fragment {

    private FragmentFacultyMarkAttendanceBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private FacultyAttendanceStudentAdapter attendanceAdapter;

    private final List<Subject> facultySubjects = new ArrayList<>();
    private final Calendar selectedCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Subject currentSubject = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFacultyMarkAttendanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        setupRecyclerView();
        setupDatePicker();
        setupQuickActionButtons();
        setupSaveButton();
        loadSubjects();
    }

    private void setupRecyclerView() {
        attendanceAdapter = new FacultyAttendanceStudentAdapter();
        binding.rvStudentAttendanceList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStudentAttendanceList.setAdapter(attendanceAdapter);
    }

    private void setupDatePicker() {
        binding.tvSelectedDate.setText(dateFormat.format(selectedCalendar.getTime()));

        View.OnClickListener dateClickListener = v -> {
            int year = selectedCalendar.get(Calendar.YEAR);
            int month = selectedCalendar.get(Calendar.MONTH);
            int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, y, m, d) -> {
                selectedCalendar.set(Calendar.YEAR, y);
                selectedCalendar.set(Calendar.MONTH, m);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, d);
                binding.tvSelectedDate.setText(dateFormat.format(selectedCalendar.getTime()));
                loadAttendanceForCurrentSelection();
            }, year, month, day);

            dialog.show();
        };

        binding.layoutPickDate.setOnClickListener(dateClickListener);
        binding.btnChangeDate.setOnClickListener(dateClickListener);
    }

    private void setupQuickActionButtons() {
        binding.btnAllPresent.setOnClickListener(v -> attendanceAdapter.setAllStatus(1));
        binding.btnAllAbsent.setOnClickListener(v -> attendanceAdapter.setAllStatus(0));
    }

    private void setupSaveButton() {
        binding.btnSaveAttendance.setOnClickListener(v -> saveAttendance());
    }

    private void loadSubjects() {
        long facultyId = sessionManager.getFacultyId();
        List<Subject> subjects = dbHelper.getSubjectsForFaculty(facultyId);
        facultySubjects.clear();

        if (subjects.isEmpty()) {
            // Fallback: all subjects if faculty ID not mapped
            subjects = dbHelper.getAllSubjects();
        }
        facultySubjects.addAll(subjects);

        List<String> subjectDisplayList = new ArrayList<>();
        for (Subject s : facultySubjects) {
            subjectDisplayList.add(String.format("%s - %s (%s)", s.getCode(), s.getName(), Constants.DEMO_CLASS_NAME));
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                subjectDisplayList
        );
        binding.spinnerFacultySubjects.setAdapter(spinnerAdapter);

        binding.spinnerFacultySubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < facultySubjects.size()) {
                    currentSubject = facultySubjects.get(position);
                    loadAttendanceForCurrentSelection();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadAttendanceForCurrentSelection() {
        if (currentSubject == null) {
            return;
        }

        String date = dateFormat.format(selectedCalendar.getTime());
        List<AttendanceRecord> records = dbHelper.getAttendanceForClassSubjectAndDate(
                currentSubject.getId(), Constants.DEMO_CLASS_NAME, date
        );

        boolean isExisting = false;
        for (AttendanceRecord r : records) {
            if (r.getId() != -1) {
                isExisting = true;
                break;
            }
        }

        if (isExisting) {
            binding.tvModeBanner.setText(String.format("Loaded existing attendance for %s. Edit below and press Save to update.", date));
            binding.tvModeBanner.setTextColor(requireContext().getColor(R.color.accent));
        } else {
            binding.tvModeBanner.setText(String.format("New attendance session for %s. Verify student presence and press Save.", date));
            binding.tvModeBanner.setTextColor(requireContext().getColor(R.color.text_secondary));
        }

        attendanceAdapter.setRecords(records);
    }

    private void saveAttendance() {
        if (currentSubject == null) {
            Snackbar.make(binding.getRoot(), "Please select a subject first", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String date = dateFormat.format(selectedCalendar.getTime());
        List<AttendanceRecord> records = attendanceAdapter.getRecords();

        if (records.isEmpty()) {
            Snackbar.make(binding.getRoot(), "No students to record attendance for", Snackbar.LENGTH_SHORT).show();
            return;
        }

        int presentCount = 0;
        for (AttendanceRecord record : records) {
            dbHelper.saveOrUpdateAttendance(
                    record.getStudentId(),
                    currentSubject.getId(),
                    date,
                    record.getStatus()
            );
            if (record.isPresent()) {
                presentCount++;
            }
        }

        Snackbar.make(binding.getRoot(),
                String.format(Locale.getDefault(), "Attendance saved for %s (%d/%d Present)", date, presentCount, records.size()),
                Snackbar.LENGTH_LONG).show();

        loadAttendanceForCurrentSelection();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
