package com.ptithcm.lottemart.features.shopping;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.Order;
import java.util.List;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.ViewHolder> {
    private final Context context;
    private final List<Order> orders;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public UserOrderAdapter(Context context, List<Order> orders, OnOrderClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderId.setText("Mã: #" + (order.getId() != null ? order.getId() : "N/A"));
        
        // Format date
        String rawDate = order.getCreatedAt();
        if (rawDate != null && rawDate.contains("T")) {
            rawDate = rawDate.replace("T", " ").substring(0, 16);
        }
        holder.tvOrderDate.setText("Ngày đặt: " + (rawDate != null ? rawDate : "Chưa xác định"));
        
        // Item count
        int itemCount = order.getItems() != null ? order.getItems().size() : 0;
        holder.tvOrderItems.setText(itemCount + " sản phẩm");
        
        // Total amount
        holder.tvOrderTotal.setText(String.format("%,.0f đ", order.getTotalAmount() + order.getShippingFee()));
        
        // Format Status
        String status = order.getStatus();
        String statusText = "Chờ xác nhận";
        int badgeColor = Color.parseColor("#FFA726"); // Orange for Pending
        
        if (status != null) {
            switch (status.toUpperCase()) {
                case "PENDING":
                    statusText = "Chờ xác nhận";
                    badgeColor = Color.parseColor("#FFA726");
                    break;
                case "ACCEPTED":
                case "PREPARING":
                    statusText = "Đang soạn hàng";
                    badgeColor = Color.parseColor("#29B6F6"); // Blue
                    break;
                case "SHIPPING":
                case "PICKED_UP":
                    statusText = "Đang giao";
                    badgeColor = Color.parseColor("#AB47BC"); // Purple
                    break;
                case "DELIVERED":
                    statusText = "Đã giao hàng";
                    badgeColor = Color.parseColor("#66BB6A"); // Green
                    break;
                case "CANCELLED":
                    statusText = "Đã hủy";
                    badgeColor = Color.parseColor("#EF5350"); // Red
                    break;
            }
        }
        holder.tvOrderStatus.setText(statusText);
        
        // Apply custom badge color using a helper or changing background tint
        try {
            holder.tvOrderStatus.getBackground().setTint(badgeColor);
        } catch (Exception e) {
            holder.tvOrderStatus.setBackgroundColor(badgeColor);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderDate, tvOrderItems, tvOrderTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}
