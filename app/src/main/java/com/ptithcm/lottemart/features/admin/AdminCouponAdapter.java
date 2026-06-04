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

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.Coupon;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminCouponAdapter extends RecyclerView.Adapter<AdminCouponAdapter.ViewHolder> {
    private Context context;
    private List<Coupon> coupons;
    private OnCouponActionListener listener;

    public interface OnCouponActionListener {
        void onEdit(Coupon coupon);
        void onDelete(Coupon coupon);
    }

    public AdminCouponAdapter(Context context, List<Coupon> coupons, OnCouponActionListener listener) {
        this.context = context;
        this.coupons = coupons;
        this.listener = listener;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        
        holder.tvCode.setText("MÃ: " + coupon.getCode().toUpperCase());
        holder.tvTitle.setText(coupon.getTitle());
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvDiscount.setText("Giảm: " + format.format(coupon.getDiscountValue()));
        
        if (coupon.isActive()) {
            holder.tvStatus.setText("Trạng thái: Hoạt động");
            holder.tvStatus.setTextColor(Color.parseColor("#388E3C"));
        } else {
            holder.tvStatus.setText("Trạng thái: Tạm dừng");
            holder.tvStatus.setTextColor(Color.RED);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(coupon);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(coupon);
        });
    }

    @Override
    public int getItemCount() {
        return coupons != null ? coupons.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvTitle, tvDiscount, tvStatus;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}


