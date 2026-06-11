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

/**
 * Adapter for displaying items in the shopping cart.
 */
public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onQuantityChanged(int position, int newQuantity);
        void onItemDeleted(int position);
    }

    public CartItemAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    public void setOnCartItemChangeListener(OnCartItemChangeListener listener) {
        this.listener = listener;
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
                    .load(com.ptithcm.lottemart.data.remote.NetworkConfig.getFullImageUrl(item.getProduct().getImageUrl()))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivProduct);
        }

        // Xử lý nút Cộng
        holder.btnPlus.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            item.setQuantity(newQty);
            holder.tvQuantity.setText(String.valueOf(newQty));
            if (listener != null) listener.onQuantityChanged(position, newQty);
        });

        // Xử lý nút Trừ
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQty = item.getQuantity() - 1;
                item.setQuantity(newQty);
                holder.tvQuantity.setText(String.valueOf(newQty));
                if (listener != null) listener.onQuantityChanged(position, newQty);
            }
        });

        // Xử lý nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onItemDeleted(position);
        });
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
        ImageView ivProduct, btnDelete;
        TextView tvName, tvPrice, tvQuantity, btnPlus, btnMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
