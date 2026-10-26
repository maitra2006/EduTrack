package com.edutrack.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edutrack.app.databinding.ItemFacultyAnnouncementBinding;
import com.edutrack.app.model.Announcement;

import java.util.ArrayList;
import java.util.List;

public class FacultyAnnouncementsAdapter extends RecyclerView.Adapter<FacultyAnnouncementsAdapter.ViewHolder> {

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
        ItemFacultyAnnouncementBinding binding = ItemFacultyAnnouncementBinding.inflate(
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
        private final ItemFacultyAnnouncementBinding binding;

        ViewHolder(ItemFacultyAnnouncementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Announcement item) {
            binding.tvFacultyNoticeTitle.setText(item.getTitle());
            binding.tvFacultyNoticeClass.setText(item.getClassName());
            binding.tvFacultyNoticeDate.setText(String.format("Posted on %s", item.getCreatedAt()));
            binding.tvFacultyNoticeMessage.setText(item.getMessage());
        }
    }
}
