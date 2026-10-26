package com.edutrack.app.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.edutrack.app.R;
import com.edutrack.app.databinding.ItemAdminAttendanceBinding;
import com.edutrack.app.model.AttendanceRecord;
import java.util.ArrayList;
import java.util.List;

public class AdminAttendanceAdapter extends RecyclerView.Adapter<AdminAttendanceAdapter.AttendanceViewHolder> {

    public interface OnAttendanceActionListener {
        void onEdit(AttendanceRecord record);
        void onDelete(AttendanceRecord record);
    }

    private List<AttendanceRecord> records = new ArrayList<>();
    private final OnAttendanceActionListener listener;

    public AdminAttendanceAdapter(OnAttendanceActionListener listener) {
        this.listener = listener;
    }

    public void setRecords(List<AttendanceRecord> records) {
        this.records = records != null ? records : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminAttendanceBinding binding = ItemAdminAttendanceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AttendanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        holder.bind(records.get(position));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminAttendanceBinding binding;

        public AttendanceViewHolder(@NonNull ItemAdminAttendanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AttendanceRecord record) {
            Context context = itemView.getContext();
            binding.tvAdminAttRoll.setText(record.getRollNo() != null ? record.getRollNo() : "-");
            binding.tvAdminAttStudentName.setText(record.getStudentName() != null ? record.getStudentName() : "Student #" + record.getStudentId());
            binding.tvAdminAttDate.setText(record.getDate());

            if (record.isPresent()) {
                binding.tvAdminAttStatus.setText("Present");
                binding.tvAdminAttStatus.setTextColor(ContextCompat.getColor(context, R.color.status_green));
                binding.tvAdminAttStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_green_bg)));
            } else {
                binding.tvAdminAttStatus.setText("Absent");
                binding.tvAdminAttStatus.setTextColor(ContextCompat.getColor(context, R.color.status_red));
                binding.tvAdminAttStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_red_bg)));
            }

            binding.btnAdminEditAttendance.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(record);
                }
            });

            binding.btnAdminDeleteAttendance.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(record);
                }
            });
        }
    }
}
