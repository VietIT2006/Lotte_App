package com.ptithcm.lottemart.features.admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.InventoryBatch;

import java.util.List;

public class AdminBatchAdapter extends RecyclerView.Adapter<AdminBatchAdapter.ViewHolder> {
    private Context context;
    private List<InventoryBatch> batches;

    public AdminBatchAdapter(Context context, List<InventoryBatch> batches) {
        this.context = context;
        this.batches = batches;
    }

    public void setbatches(List<InventoryBatch> batches) {
        this.batches = batches;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_batch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryBatch batch = batches.get(position);
        
        holder.tvBatchCode.setText(batch.getBatchCode());
        holder.tvProduct.setText("products: " + batch.getProductName());
        holder.tvSupplier.setText("NCC: " + batch.getSupplierName());
        
        holder.tvQuantity.setText("Còn lại: " + batch.getQuantity() + "/" + batch.getOriginalQuantity());
        if (batch.getQuantity() <= 0) {
            holder.tvQuantity.setTextColor(Color.RED);
            holder.tvQuantity.setText("Hết hàng (0/" + batch.getOriginalQuantity() + ")");
        } else {
            holder.tvQuantity.setTextColor(Color.parseColor("#388E3C"));
        }

        String received = batch.getReceivedDate() != null ? batch.getReceivedDate().substring(0, 10) : "?";
        String exp = batch.getExpDate() != null ? batch.getExpDate().substring(0, 10) : "?";
        holder.tvDates.setText("Nhập: " + received + " - HSD: " + exp);
    }

    @Override
    public int getItemCount() {
        return batches != null ? batches.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBatchCode, tvQuantity, tvProduct, tvSupplier, tvDates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBatchCode = itemView.findViewById(R.id.tvBatchCode);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvSupplier = itemView.findViewById(R.id.tvSupplier);
            tvDates = itemView.findViewById(R.id.tvDates);
        }
    }
}


