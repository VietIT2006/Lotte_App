package com.ptithcm.lottemart.features.notifications;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private Context context;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> notifications, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvTime.setText(notification.getCreatedAt()); // Giả định format đã ok từ server

        // Xử lý trạng thái đã đọc
        if (notification.isRead()) {
            holder.viewUnread.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.7f);
        } else {
            holder.viewUnread.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);
        }

        // Thay đổi icon dựa trên type
        if ("ORDER".equals(notification.getType())) {
            holder.ivIcon.setImageResource(R.drawable.ic_cart);
            holder.iconContainer.setBackgroundResource(R.drawable.bg_circle_blue);
        } else if ("PROMO".equals(notification.getType())) {
            holder.ivIcon.setImageResource(R.drawable.ic_categories);
            holder.iconContainer.setBackgroundResource(R.drawable.bg_circle_orange);
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_notification);
            holder.iconContainer.setBackgroundResource(R.drawable.bg_circle_red);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNotificationClick(notification);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        ImageView ivIcon;
        View viewUnread, iconContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            viewUnread = itemView.findViewById(R.id.viewUnread);
            iconContainer = itemView.findViewById(R.id.iconContainer);
        }
    }
}
