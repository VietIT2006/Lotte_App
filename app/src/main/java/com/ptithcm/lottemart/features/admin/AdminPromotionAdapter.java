package com.ptithcm.lottemart.features.admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.Promotion;

import java.util.List;

public class AdminPromotionAdapter extends RecyclerView.Adapter<AdminPromotionAdapter.ViewHolder> {
    private Context context;
    private List<Promotion> promotions;
    private OnPromotionActionListener listener;

    public interface OnPromotionActionListener {
        void onEdit(Promotion promotion);
        void onDelete(Promotion promotion);
    }

    public AdminPromotionAdapter(Context context, List<Promotion> promotions, OnPromotionActionListener listener) {
        this.context = context;
        this.promotions = promotions;
        this.listener = listener;
    }

    public void setpromotions(List<Promotion> promotions) {
        this.promotions = promotions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_promotion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Promotion promotion = promotions.get(position);

        holder.tvTitle.setText(promotion.getTitle());
        holder.tvDescription.setText(promotion.getDescription());

        if (promotion.isActive()) {
            holder.tvStatus.setText("Đang hoạt động");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#C8E6C9"));
            holder.tvStatus.setTextColor(Color.parseColor("#388E3C"));
        } else {
            holder.tvStatus.setText("Đã ẩn");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFCDD2"));
            holder.tvStatus.setTextColor(Color.parseColor("#D32F2F"));
        }

        Glide.with(context)
                .load(promotion.getBannerImage())
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .into(holder.ivBanner);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(promotion);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(promotion);
        });
    }

    @Override
    public int getItemCount() {
        return promotions != null ? promotions.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner, btnEdit, btnDelete;
        TextView tvTitle, tvStatus, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}


