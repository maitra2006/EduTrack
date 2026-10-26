package com.edutrack.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.edutrack.app.databinding.ItemAdminStudentBinding;
import com.edutrack.app.model.Student;
import java.util.ArrayList;
import java.util.List;

public class AdminStudentsAdapter extends RecyclerView.Adapter<AdminStudentsAdapter.StudentViewHolder> {

    public interface OnStudentActionListener {
        void onEdit(Student student);
        void onDelete(Student student);
    }

    private List<Student> studentList = new ArrayList<>();
    private final OnStudentActionListener listener;

    public AdminStudentsAdapter(OnStudentActionListener listener) {
        this.listener = listener;
    }

    public void setStudents(List<Student> students) {
        this.studentList = students != null ? students : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminStudentBinding binding = ItemAdminStudentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StudentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.bind(studentList.get(position));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminStudentBinding binding;

        public StudentViewHolder(@NonNull ItemAdminStudentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Student student) {
            binding.tvAdminStudentRoll.setText(student.getRollNo());
            binding.tvAdminStudentName.setText(student.getName());
            binding.tvAdminStudentEmail.setText(student.getEmail());
            binding.tvAdminStudentClass.setText("Class: " + student.getClassName());

            binding.btnAdminEditStudent.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(student);
                }
            });

            binding.btnAdminDeleteStudent.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(student);
                }
            });
        }
    }
}
