package com.edutrack.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.edutrack.app.databinding.ItemAdminSubjectBinding;
import com.edutrack.app.model.Subject;
import java.util.ArrayList;
import java.util.List;

public class AdminSubjectsAdapter extends RecyclerView.Adapter<AdminSubjectsAdapter.SubjectViewHolder> {

    public interface OnSubjectActionListener {
        void onEdit(Subject subject);
        void onDelete(Subject subject);
    }

    private List<Subject> subjectList = new ArrayList<>();
    private final OnSubjectActionListener listener;

    public AdminSubjectsAdapter(OnSubjectActionListener listener) {
        this.listener = listener;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjectList = subjects != null ? subjects : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminSubjectBinding binding = ItemAdminSubjectBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SubjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        holder.bind(subjectList.get(position));
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminSubjectBinding binding;

        public SubjectViewHolder(@NonNull ItemAdminSubjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Subject subject) {
            binding.tvAdminSubjectCode.setText(subject.getCode());
            binding.tvAdminSubjectName.setText(subject.getName());
            binding.tvAdminSubjectDept.setText(subject.getDepartment());

            binding.btnAdminEditSubject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(subject);
                }
            });

            binding.btnAdminDeleteSubject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(subject);
                }
            });
        }
    }
}
