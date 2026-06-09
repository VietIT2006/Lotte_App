package com.ptithcm.lottemart.features.shopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.Coupon;
import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {
    private final Context context;
    private final List<Coupon> coupons;
    private final OnCouponClickListener listener;

    public interface OnCouponClickListener {
        void onApply(Coupon coupon);
    }

    public CouponAdapter(Context context, List<Coupon> coupons, OnCouponClickListener listener) {
        this.context = context;
        this.coupons = coupons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        holder.tvCouponCode.setText(coupon.getCode());
        holder.tvCouponDesc.setText(coupon.getTitle() != null ? coupon.getTitle() : "Giảm giá hấp dẫn cho đơn hàng");
        holder.tvCouponExpiry.setText("Mức giảm: " + String.format("%,.0fđ", coupon.getDiscountValue()));

        holder.btnApply.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApply(coupon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCouponCode, tvCouponDesc, tvCouponExpiry;
        MaterialButton btnApply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCouponCode = itemView.findViewById(R.id.tvCouponCode);
            tvCouponDesc = itemView.findViewById(R.id.tvCouponDesc);
            tvCouponExpiry = itemView.findViewById(R.id.tvCouponExpiry);
            btnApply = itemView.findViewById(R.id.btnApply);
        }
    }
}
