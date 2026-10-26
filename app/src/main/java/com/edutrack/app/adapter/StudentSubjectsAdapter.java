package com.edutrack.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edutrack.app.databinding.ItemStudentSubjectBinding;
import com.edutrack.app.model.Subject;

import java.util.ArrayList;
import java.util.List;

public class StudentSubjectsAdapter extends RecyclerView.Adapter<StudentSubjectsAdapter.ViewHolder> {

    private final List<Subject> list = new ArrayList<>();

    public void setList(List<Subject> newList) {
        list.clear();
        if (newList != null) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentSubjectBinding binding = ItemStudentSubjectBinding.inflate(
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
        private final ItemStudentSubjectBinding binding;

        ViewHolder(ItemStudentSubjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Subject subject) {
            binding.tvSubjectCode.setText(subject.getCode());
            binding.tvSubjectTitle.setText(subject.getName());
            binding.tvSubjectDepartment.setText(subject.getDepartment());
            binding.tvFacultyAssigned.setText(
                    subject.getFacultyName() != null && !subject.getFacultyName().isEmpty()
                            ? "Faculty: " + subject.getFacultyName()
                            : "Faculty: Assigned Department Staff"
            );
        }
    }
}
