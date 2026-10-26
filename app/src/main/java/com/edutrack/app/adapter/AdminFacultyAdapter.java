package com.edutrack.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.edutrack.app.databinding.ItemAdminFacultyBinding;
import com.edutrack.app.model.Faculty;
import java.util.ArrayList;
import java.util.List;

public class AdminFacultyAdapter extends RecyclerView.Adapter<AdminFacultyAdapter.FacultyViewHolder> {

    public interface OnFacultyActionListener {
        void onEdit(Faculty faculty);
        void onDelete(Faculty faculty);
    }

    private List<Faculty> facultyList = new ArrayList<>();
    private final OnFacultyActionListener listener;

    public AdminFacultyAdapter(OnFacultyActionListener listener) {
        this.listener = listener;
    }

    public void setFacultyList(List<Faculty> facultyList) {
        this.facultyList = facultyList != null ? facultyList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminFacultyBinding binding = ItemAdminFacultyBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FacultyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        holder.bind(facultyList.get(position));
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }

    class FacultyViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminFacultyBinding binding;

        public FacultyViewHolder(@NonNull ItemAdminFacultyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Faculty faculty) {
            binding.tvAdminFacultyName.setText(faculty.getName());
            binding.tvAdminFacultyDesignation.setText(faculty.getDesignation() + " - " + faculty.getDepartment());
            binding.tvAdminFacultyEmail.setText(faculty.getEmail());

            binding.btnAdminEditFaculty.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(faculty);
                }
            });

            binding.btnAdminDeleteFaculty.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(faculty);
                }
            });
        }
    }
}
