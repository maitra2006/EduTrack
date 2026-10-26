package com.edutrack.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edutrack.app.databinding.ItemFacultyClassBinding;
import com.edutrack.app.model.Subject;
import com.edutrack.app.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class FacultyClassesAdapter extends RecyclerView.Adapter<FacultyClassesAdapter.ViewHolder> {

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
        ItemFacultyClassBinding binding = ItemFacultyClassBinding.inflate(
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
        private final ItemFacultyClassBinding binding;

        ViewHolder(ItemFacultyClassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Subject subject) {
            binding.tvFacultySubjectCode.setText(subject.getCode());
            binding.tvFacultySubjectName.setText(subject.getName());
            binding.tvFacultyDept.setText("Department: " + subject.getDepartment());
            binding.tvFacultyClassName.setText("Class: " + Constants.DEMO_CLASS_NAME);
        }
    }
}
