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
import com.ptithcm.lottemart.data.models.Category;
import android.graphics.drawable.ColorDrawable;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        layout.setLayoutParams(new LinearLayout.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView image = new ImageView(context);
        image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
        image.setBackgroundColor(Color.LTGRAY);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        TextView name = new TextView(context);
        name.setTextSize(12);
        name.setGravity(android.view.Gravity.CENTER);
        name.setPadding(0, 8, 0, 0);

        layout.addView(image);
        layout.addView(name);

        return new ViewHolder(layout, image, name);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.name.setText(category.getName());

        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(category.getImageUrl())
                    .placeholder(new ColorDrawable(Color.LTGRAY))
                    .into(holder.image);
        } else {
            holder.image.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        public ViewHolder(@NonNull LinearLayout itemView, ImageView image, TextView name) {
            super(itemView);
            this.image = image;
            this.name = name;
        }
    }
}
