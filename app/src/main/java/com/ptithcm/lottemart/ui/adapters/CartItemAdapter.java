package com.ptithcm.lottemart.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        layout.setLayoutParams(params);
        layout.setGravity(Gravity.CENTER_VERTICAL);

        ImageView image = new ImageView(context);
        image.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
        image.setBackgroundColor(Color.LTGRAY);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f
        );
        textParams.setMargins(32, 0, 0, 0);
        textLayout.setLayoutParams(textParams);

        TextView name = new TextView(context);
        name.setTextSize(16);
        name.setTextColor(Color.BLACK);

        TextView price = new TextView(context);
        price.setTextSize(14);
        price.setTextColor(Color.parseColor("#E1251B"));
        
        textLayout.addView(name);
        textLayout.addView(price);

        TextView quantity = new TextView(context);
        quantity.setTextSize(16);
        quantity.setTextColor(Color.BLACK);
        quantity.setPadding(32, 0, 0, 0);

        layout.addView(image);
        layout.addView(textLayout);
        layout.addView(quantity);

        return new ViewHolder(layout, image, name, price, quantity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.name.setText(item.getProduct().getName());
        holder.price.setText(String.format("%,.0f đ", item.getProduct().getPrice()));
        holder.quantity.setText("x" + item.getQuantity());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        public ViewHolder(@NonNull LinearLayout itemView, ImageView image, TextView name, TextView price, TextView quantity) {
            super(itemView);
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }
}
