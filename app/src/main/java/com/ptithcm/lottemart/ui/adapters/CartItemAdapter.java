package com.ptithcm.lottemart.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.CartItem;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private Context context;

    public CartItemAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvName.setText(item.getProduct().getName());
        holder.tvPrice.setText(String.format("%,.0f đ", item.getProduct().getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        if (item.getProduct().getImageUrl() != null && !item.getProduct().getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getProduct().getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivProduct);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void setItems(List<CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
