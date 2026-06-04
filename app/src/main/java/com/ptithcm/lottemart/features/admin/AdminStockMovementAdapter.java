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
import com.ptithcm.lottemart.data.models.StockMovement;

import java.util.List;

public class AdminStockMovementAdapter extends RecyclerView.Adapter<AdminStockMovementAdapter.ViewHolder> {
    private Context context;
    private List<StockMovement> movements;

    public AdminStockMovementAdapter(Context context, List<StockMovement> movements) {
        this.context = context;
        this.movements = movements;
    }

    public void setMovements(List<StockMovement> movements) {
        this.movements = movements;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_stock_movement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockMovement movement = movements.get(position);

        holder.tvProductName.setText(movement.getProductName());
        holder.tvMovementType.setText(movement.getMovementType());
        
        String sign = movement.getQuantity() > 0 ? "+" : "";
        holder.tvQuantity.setText(sign + movement.getQuantity());
        if (movement.getQuantity() > 0) {
            holder.tvQuantity.setTextColor(Color.parseColor("#388E3C")); // Green
        } else {
            holder.tvQuantity.setTextColor(Color.parseColor("#D32F2F")); // Red
        }

        holder.tvStockDetail.setText(movement.getBeforeStock() + " -> " + movement.getAfterStock());

        if (movement.getNote() != null && !movement.getNote().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText("Ghi chú: " + movement.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return movements != null ? movements.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvMovementType, tvQuantity, tvStockDetail, tvNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvMovementType = itemView.findViewById(R.id.tvMovementType);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvStockDetail = itemView.findViewById(R.id.tvStockDetail);
            tvNote = itemView.findViewById(R.id.tvNote);
        }
    }
}
