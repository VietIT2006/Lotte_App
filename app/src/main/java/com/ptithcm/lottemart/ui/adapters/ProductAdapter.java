package com.ptithcm.lottemart.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ptithcm.lottemart.data.models.Product;
import android.graphics.drawable.ColorDrawable;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> products, OnItemClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        layout.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 16, 16, 16);
        layout.setLayoutParams(params);

        ImageView image = new ImageView(context);
        image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        image.setBackgroundColor(Color.LTGRAY);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        TextView name = new TextView(context);
        name.setTextSize(14);
        name.setTextColor(Color.DKGRAY);
        name.setPadding(0, 16, 0, 8);
        name.setMaxLines(2);

        TextView price = new TextView(context);
        price.setTextSize(16);
        price.setTextColor(Color.parseColor("#E1251B")); // Lotte Red

        layout.addView(image);
        layout.addView(name);
        layout.addView(price);

        return new ViewHolder(layout, image, name, price);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(String.format("%,.0f đ", product.getPrice()));

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(new ColorDrawable(Color.LTGRAY))
                    .into(holder.image);
        } else {
            holder.image.setBackgroundColor(Color.LTGRAY);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if(listener != null) listener.onItemClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price;
        public ViewHolder(@NonNull LinearLayout itemView, ImageView image, TextView name, TextView price) {
            super(itemView);
            this.image = image;
            this.name = name;
            this.price = price;
        }
    }
}
