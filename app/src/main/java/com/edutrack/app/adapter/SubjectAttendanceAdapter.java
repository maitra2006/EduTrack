package com.edutrack.app.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.edutrack.app.R;
import com.edutrack.app.databinding.ItemStudentSubjectAttendanceBinding;
import com.edutrack.app.model.SubjectAttendanceSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubjectAttendanceAdapter extends RecyclerView.Adapter<SubjectAttendanceAdapter.ViewHolder> {

    private final List<SubjectAttendanceSummary> list = new ArrayList<>();

    public void setList(List<SubjectAttendanceSummary> newList) {
        list.clear();
        if (newList != null) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentSubjectAttendanceBinding binding = ItemStudentSubjectAttendanceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemStudentSubjectAttendanceBinding binding;

        ViewHolder(ItemStudentSubjectAttendanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SubjectAttendanceSummary item) {
            Context context = itemView.getContext();
            binding.tvSubjectCodeBadge.setText(item.getSubjectCode());
            binding.tvSubjectName.setText(item.getSubjectName());
            binding.tvFacultyName.setText(String.format("Faculty: %s", item.getFacultyName()));

            binding.tvLectureStats.setText(String.format(Locale.getDefault(),
                    "%d of %d Lectures Attended", item.getAttendedLectures(), item.getTotalLectures()));

            double pct = item.getPercentage();
            binding.tvPercentageBadge.setText(String.format(Locale.getDefault(), "%.1f%%", pct));
            binding.progressBarAttendance.setProgress((int) Math.round(pct));

            int colorPrimary = ContextCompat.getColor(context, R.color.primary);
            int colorGreen = ContextCompat.getColor(context, R.color.status_green);
            int colorGreenBg = ContextCompat.getColor(context, R.color.status_green_bg);
            int colorAmber = ContextCompat.getColor(context, R.color.status_amber);
            int colorAmberBg = ContextCompat.getColor(context, R.color.status_amber_bg);
            int colorRed = ContextCompat.getColor(context, R.color.status_red);
            int colorRedBg = ContextCompat.getColor(context, R.color.status_red_bg);

            if (item.isOnTrack()) {
                // >= 75%
                binding.tvPercentageBadge.setTextColor(colorGreen);
                binding.tvPercentageBadge.setBackgroundTintList(ColorStateList.valueOf(colorGreenBg));
                binding.progressBarAttendance.setIndicatorColor(colorGreen);

                binding.ivStatusIcon.setImageResource(R.drawable.ic_check_circle);
                binding.ivStatusIcon.setImageTintList(ColorStateList.valueOf(colorGreen));
                binding.tvTargetRecommendation.setText("You're on track (above 75% requirement)");
                binding.tvTargetRecommendation.setTextColor(colorPrimary);
            } else if (item.isWarning()) {
                // 65% - 74.9%
                binding.tvPercentageBadge.setTextColor(colorAmber);
                binding.tvPercentageBadge.setBackgroundTintList(ColorStateList.valueOf(colorAmberBg));
                binding.progressBarAttendance.setIndicatorColor(colorAmber);

                binding.ivStatusIcon.setImageResource(R.drawable.ic_warning);
                binding.ivStatusIcon.setImageTintList(ColorStateList.valueOf(colorAmber));
                binding.tvTargetRecommendation.setText(String.format(Locale.getDefault(),
                        "Need %d more consecutive lecture%s to reach 75%%",
                        item.getRequiredLecturesTo75(),
                        item.getRequiredLecturesTo75() == 1 ? "" : "s"));
                binding.tvTargetRecommendation.setTextColor(colorAmber);
            } else {
                // < 65%
                binding.tvPercentageBadge.setTextColor(colorRed);
                binding.tvPercentageBadge.setBackgroundTintList(ColorStateList.valueOf(colorRedBg));
                binding.progressBarAttendance.setIndicatorColor(colorRed);

                binding.ivStatusIcon.setImageResource(R.drawable.ic_warning);
                binding.ivStatusIcon.setImageTintList(ColorStateList.valueOf(colorRed));
                binding.tvTargetRecommendation.setText(String.format(Locale.getDefault(),
                        "Critical: Need %d more consecutive lecture%s to reach 75%%",
                        item.getRequiredLecturesTo75(),
                        item.getRequiredLecturesTo75() == 1 ? "" : "s"));
                binding.tvTargetRecommendation.setTextColor(colorRed);
            }
        }
    }
}
