package com.edutrack.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.edutrack.app.databinding.ItemAdminAnnouncementBinding;
import com.edutrack.app.model.Announcement;
import java.util.ArrayList;
import java.util.List;

public class AdminAnnouncementsAdapter extends RecyclerView.Adapter<AdminAnnouncementsAdapter.AnnouncementViewHolder> {

    public interface OnAnnouncementActionListener {
        void onDelete(Announcement announcement);
    }

    private List<Announcement> announcements = new ArrayList<>();
    private final OnAnnouncementActionListener listener;

    public AdminAnnouncementsAdapter(OnAnnouncementActionListener listener) {
        this.listener = listener;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements != null ? announcements : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminAnnouncementBinding binding = ItemAdminAnnouncementBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AnnouncementViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        holder.bind(announcements.get(position));
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminAnnouncementBinding binding;

        public AnnouncementViewHolder(@NonNull ItemAdminAnnouncementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Announcement announcement) {
            binding.tvAdminNoticeTitle.setText(announcement.getTitle());
            String meta = "By " + announcement.getFacultyName() + " | " + announcement.getClassName();
            binding.tvAdminNoticeMeta.setText(meta);
            binding.tvAdminNoticeBody.setText(announcement.getMessage());

            binding.btnAdminDeleteNotice.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(announcement);
                }
            });
        }
    }
}
