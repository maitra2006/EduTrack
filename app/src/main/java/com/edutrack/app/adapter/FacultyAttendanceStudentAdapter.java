package com.edutrack.app.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.edutrack.app.R;
import com.edutrack.app.databinding.ItemFacultyStudentAttendanceBinding;
import com.edutrack.app.model.AttendanceRecord;

import java.util.ArrayList;
import java.util.List;

public class FacultyAttendanceStudentAdapter extends RecyclerView.Adapter<FacultyAttendanceStudentAdapter.ViewHolder> {

    private final List<AttendanceRecord> records = new ArrayList<>();

    public void setRecords(List<AttendanceRecord> newRecords) {
        records.clear();
        if (newRecords != null) {
            records.addAll(newRecords);
        }
        notifyDataSetChanged();
    }

    public List<AttendanceRecord> getRecords() {
        return records;
    }

    public void setAllStatus(int status) {
        for (AttendanceRecord record : records) {
            record.setStatus(status);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFacultyStudentAttendanceBinding binding = ItemFacultyStudentAttendanceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(records.get(position));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFacultyStudentAttendanceBinding binding;

        ViewHolder(ItemFacultyStudentAttendanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AttendanceRecord record) {
            Context context = itemView.getContext();
            binding.tvStudentRollNo.setText(record.getRollNo());
            binding.tvStudentName.setText(record.getStudentName());

            // Remove listener before setting checked state to avoid recursion
            binding.switchAttendance.setOnCheckedChangeListener(null);
            binding.switchAttendance.setChecked(record.isPresent());

            updateStatusUI(context, record.isPresent());

            binding.switchAttendance.setOnCheckedChangeListener((buttonView, isChecked) -> {
                record.setStatus(isChecked ? 1 : 0);
                updateStatusUI(context, isChecked);
            });
        }

        private void updateStatusUI(Context context, boolean isPresent) {
            int colorGreen = ContextCompat.getColor(context, R.color.status_green);
            int colorRed = ContextCompat.getColor(context, R.color.status_red);

            if (isPresent) {
                binding.tvStatusLabel.setText("Present");
                binding.tvStatusLabel.setTextColor(colorGreen);
                binding.switchAttendance.setTrackTintList(ColorStateList.valueOf(colorGreen));
            } else {
                binding.tvStatusLabel.setText("Absent");
                binding.tvStatusLabel.setTextColor(colorRed);
                binding.switchAttendance.setTrackTintList(ColorStateList.valueOf(colorRed));
            }
        }
    }
}
