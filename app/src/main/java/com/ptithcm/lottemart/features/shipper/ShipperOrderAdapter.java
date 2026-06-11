package com.ptithcm.lottemart.features.shipper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.ShipperOrder;
import java.util.ArrayList;
import java.util.List;

public class ShipperOrderAdapter extends RecyclerView.Adapter<ShipperOrderAdapter.OrderViewHolder> {

    private List<ShipperOrder> orderList = new ArrayList<>();
    private OnOrderActionClickListener listener;
    private boolean isHistoryMode = false;

    public interface OnOrderActionClickListener {
        void onActionClick(ShipperOrder order);
    }

    public void setHistoryMode(boolean historyMode) {
        this.isHistoryMode = historyMode;
    }

    public void setListener(OnOrderActionClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<ShipperOrder> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shipper_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        ShipperOrder order = orderList.get(position);
        
        String idStr = order.getId();
        String displayId = (idStr != null && idStr.length() >= 8) ? idStr.substring(0, 8) : (idStr != null ? idStr : "");
        holder.tvOrderId.setText("Mã đơn: #" + displayId);
        
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
        if (order.getPaymentMethod() != null && !"COD".equalsIgnoreCase(order.getPaymentMethod())) {
            holder.tvOrderPrice.setText("0đ (Đã thanh toán " + order.getPaymentMethod() + ")");
        } else if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            holder.tvOrderPrice.setText("0đ (Đã thanh toán)");
        } else {
            holder.tvOrderPrice.setText(format.format(order.getTotalAmount()) + " (Thu COD)");
        }
        
        if (order.getBranchName() != null) {
            // Can customize origin hub name dynamically if needed
        }
        
        if (order.getBranchAddress() != null) {
            // Can customize origin hub address dynamically if needed
        }
        
        if (order.getCreatedAt() != null) {
            // Can format date if needed
        }
        
        // Safely retrieve address description
        Object addrObj = null;
        try {
            // Try accessing field directly or parsing
            addrObj = order.getClass().getDeclaredField("order_address").get(order);
        } catch (Exception e) {
            // Fallback
        }
        if (addrObj != null) {
            holder.tvOrderAddress.setText(addrObj.toString());
        } else {
            holder.tvOrderAddress.setText("Địa chỉ khách hàng");
        }
        
        if (isHistoryMode) {
            holder.btnUpdateStatus.setText(order.getStatus().equalsIgnoreCase("delivery_failed") ? "Thất bại" : "Hoàn thành");
            holder.btnUpdateStatus.setEnabled(false);
            if (order.getStatus().equalsIgnoreCase("delivery_failed")) {
                holder.btnUpdateStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D32F2F")));
            } else {
                holder.btnUpdateStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#388E3C")));
            }
        } else {
            holder.btnUpdateStatus.setText("View Details >");
            holder.btnUpdateStatus.setEnabled(true);
            holder.btnUpdateStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#C00004")));
            holder.btnUpdateStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActionClick(order);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderAddress, tvOrderPrice;
        Button btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderAddress = itemView.findViewById(R.id.tvOrderAddress);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}
