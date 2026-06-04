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
import com.ptithcm.lottemart.data.models.ImportOrder;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminImportOrderAdapter extends RecyclerView.Adapter<AdminImportOrderAdapter.ViewHolder> {
    private Context context;
    private List<ImportOrder> orders;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onReceive(ImportOrder order);
    }

    public AdminImportOrderAdapter(Context context, List<ImportOrder> orders, OnOrderActionListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    public void setorders(List<ImportOrder> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_import_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImportOrder order = orders.get(position);
        
        holder.tvOrderCode.setText("Mã phiếu: " + order.getOrderCode());
        holder.tvSupplier.setText("NCC: " + order.getSupplierName());
        
        try {
            String shortDate = order.getCreatedAt().substring(0, 10);
            holder.tvDate.setText("Ngày tạo: " + shortDate);
        } catch (Exception e) {
            holder.tvDate.setText("Ngày tạo: " + order.getCreatedAt());
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvTotalAmount.setText("Tổng tiền: " + format.format(order.getTotalAmount()));

        holder.tvStatus.setText(order.getStatus().toUpperCase());
        if ("pending".equalsIgnoreCase(order.getStatus())) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFE0B2"));
            holder.tvStatus.setTextColor(Color.parseColor("#F57C00"));
            holder.btnReceive.setVisibility(View.VISIBLE);
        } else if ("received".equalsIgnoreCase(order.getStatus())) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#C8E6C9"));
            holder.tvStatus.setTextColor(Color.parseColor("#388E3C"));
            holder.btnReceive.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#EEEEEE"));
            holder.tvStatus.setTextColor(Color.parseColor("#757575"));
            holder.btnReceive.setVisibility(View.GONE);
        }

        holder.btnReceive.setOnClickListener(v -> {
            if (listener != null) listener.onReceive(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvStatus, tvSupplier, tvDate, tvTotalAmount;
        Button btnReceive;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSupplier = itemView.findViewById(R.id.tvSupplier);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            btnReceive = itemView.findViewById(R.id.btnReceive);
        }
    }
}


