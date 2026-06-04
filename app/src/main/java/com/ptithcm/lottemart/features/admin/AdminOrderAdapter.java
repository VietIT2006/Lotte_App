package com.ptithcm.lottemart.features.admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.Order;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {
    private Context context;
    private List<Order> orders;
    private OnorderstatusUpdateListener listener;

    public interface OnorderstatusUpdateListener {
        void onUpdateStatus(Order order);
    }

    public AdminOrderAdapter(Context context, List<Order> orders, OnorderstatusUpdateListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    public void setorders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        
        holder.tvOrderId.setText("Mã ĐH: " + (order.getId() != null ? order.getId().substring(0, 8) + "..." : ""));
        holder.tvOrderDate.setText("Ngày đặt: " + order.getCreatedAt());
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvOrderTotal.setText("Tổng tiền: " + format.format(order.getTotalAmount() + order.getShippingFee()));
        
        holder.tvOrderStatus.setText(order.getStatus());
        
        switch (order.getStatus()) {
            case "PENDING":
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#FFE0B2"));
                holder.tvOrderStatus.setTextColor(Color.parseColor("#F57C00"));
                break;
            case "PROCESSING":
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#BBDEFB"));
                holder.tvOrderStatus.setTextColor(Color.parseColor("#1976D2"));
                break;
            case "SHIPPING":
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#E1BEE7"));
                holder.tvOrderStatus.setTextColor(Color.parseColor("#7B1FA2"));
                break;
            case "COMPLETED":
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#C8E6C9"));
                holder.tvOrderStatus.setTextColor(Color.parseColor("#388E3C"));
                break;
            case "CANCELLED":
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#FFCDD2"));
                holder.tvOrderStatus.setTextColor(Color.parseColor("#D32F2F"));
                break;
            default:
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#EEEEEE"));
                holder.tvOrderStatus.setTextColor(Color.parseColor("#757575"));
                break;
        }

        holder.btnUpdateStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdateStatus(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderDate, tvOrderTotal;
        Button btnUpdateStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}


