package com.edutrack.app.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.edutrack.app.R;
import com.edutrack.app.databinding.ItemStudentAlertBinding;
import com.edutrack.app.model.SubjectAttendanceSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentAlertsAdapter extends RecyclerView.Adapter<StudentAlertsAdapter.ViewHolder> {

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
        ItemStudentAlertBinding binding = ItemStudentAlertBinding.inflate(
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
        private final ItemStudentAlertBinding binding;

        ViewHolder(ItemStudentAlertBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SubjectAttendanceSummary item) {
            Context context = itemView.getContext();
            double pct = item.getPercentage();
            int required = item.getRequiredLecturesTo75();

            binding.tvAlertSubject.setText(String.format("%s - %s", item.getSubjectCode(), item.getSubjectName()));
            binding.tvAlertPercentage.setText(String.format(Locale.getDefault(), "%.1f%%", pct));

            int colorRed = ContextCompat.getColor(context, R.color.status_red);
            int colorRedBg = ContextCompat.getColor(context, R.color.status_red_bg);
            int colorAmber = ContextCompat.getColor(context, R.color.status_amber);
            int colorAmberBg = ContextCompat.getColor(context, R.color.status_amber_bg);
            int colorGreen = ContextCompat.getColor(context, R.color.status_green);
            int colorGreenBg = ContextCompat.getColor(context, R.color.status_green_bg);

            if (item.isCritical()) {
                // < 65%
                binding.cardAlert.setStrokeColor(colorRed);
                binding.ivAlertSeverityIcon.setImageResource(R.drawable.ic_warning);
                binding.ivAlertSeverityIcon.setImageTintList(ColorStateList.valueOf(colorRed));

                binding.tvAlertSeverityBadge.setText("CRITICAL DEFICIT (< 65%)");
                binding.tvAlertSeverityBadge.setTextColor(colorRed);
                binding.tvAlertSeverityBadge.setBackgroundTintList(ColorStateList.valueOf(colorRedBg));
                binding.tvAlertPercentage.setTextColor(colorRed);

                binding.tvAlertMessage.setText(String.format(Locale.getDefault(),
                        "Severe attendance deficit! You have only attended %d of %d lectures (%.1f%%).",
                        item.getAttendedLectures(), item.getTotalLectures(), pct));

                binding.tvAlertRecommendation.setText(String.format(Locale.getDefault(),
                        "Mandatory Action: You must attend the next %d consecutive lectures without fail to reach the minimum 75%% rule.",
                        required));
            } else if (item.isWarning()) {
                // 65% - 74.9%
                binding.cardAlert.setStrokeColor(colorAmber);
                binding.ivAlertSeverityIcon.setImageResource(R.drawable.ic_warning);
                binding.ivAlertSeverityIcon.setImageTintList(ColorStateList.valueOf(colorAmber));

                binding.tvAlertSeverityBadge.setText("BORDERLINE DEFICIT (65-74%)");
                binding.tvAlertSeverityBadge.setTextColor(colorAmber);
                binding.tvAlertSeverityBadge.setBackgroundTintList(ColorStateList.valueOf(colorAmberBg));
                binding.tvAlertPercentage.setTextColor(colorAmber);

                binding.tvAlertMessage.setText(String.format(Locale.getDefault(),
                        "Attendance warning! You have attended %d of %d lectures (%.1f%%), just under the required 75%% threshold.",
                        item.getAttendedLectures(), item.getTotalLectures(), pct));

                binding.tvAlertRecommendation.setText(String.format(Locale.getDefault(),
                        "Recommended Action: Attend the next %d consecutive lecture%s to restore compliance.",
                        required, required == 1 ? "" : "s"));
            } else {
                // On track (>= 75%)
                binding.cardAlert.setStrokeColor(ContextCompat.getColor(context, R.color.card_stroke));
                binding.ivAlertSeverityIcon.setImageResource(R.drawable.ic_check_circle);
                binding.ivAlertSeverityIcon.setImageTintList(ColorStateList.valueOf(colorGreen));

                binding.tvAlertSeverityBadge.setText("COMPLIANT (≥ 75%)");
                binding.tvAlertSeverityBadge.setTextColor(colorGreen);
                binding.tvAlertSeverityBadge.setBackgroundTintList(ColorStateList.valueOf(colorGreenBg));
                binding.tvAlertPercentage.setTextColor(colorGreen);

                binding.tvAlertMessage.setText(String.format(Locale.getDefault(),
                        "Good standing! You have attended %d of %d lectures (%.1f%%).",
                        item.getAttendedLectures(), item.getTotalLectures(), pct));

                binding.tvAlertRecommendation.setText("Maintain your regular attendance to remain above 75%.");
            }
        }
    }
}
