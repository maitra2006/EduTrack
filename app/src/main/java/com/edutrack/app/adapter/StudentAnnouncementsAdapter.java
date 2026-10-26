package com.edutrack.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edutrack.app.databinding.ItemStudentAnnouncementBinding;
import com.edutrack.app.model.Announcement;

import java.util.ArrayList;
import java.util.List;

public class StudentAnnouncementsAdapter extends RecyclerView.Adapter<StudentAnnouncementsAdapter.ViewHolder> {

    private final List<Announcement> list = new ArrayList<>();

    public void setList(List<Announcement> newList) {
        list.clear();
        if (newList != null) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentAnnouncementBinding binding = ItemStudentAnnouncementBinding.inflate(
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
        private final ItemStudentAnnouncementBinding binding;

        ViewHolder(ItemStudentAnnouncementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Announcement announcement) {
            binding.tvAnnouncementTitle.setText(announcement.getTitle());
            binding.tvAnnouncementAuthor.setText(String.format("By %s", announcement.getFacultyName()));
            binding.tvAnnouncementDate.setText(announcement.getCreatedAt());
            binding.tvAnnouncementBody.setText(announcement.getMessage());
        }
    }
}
