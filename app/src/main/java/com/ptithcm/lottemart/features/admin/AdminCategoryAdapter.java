package com.ptithcm.lottemart.features.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Category;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private boolean isSuperAdmin;

    public AdminCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        SessionManager sessionManager = new SessionManager(context);
        this.isSuperAdmin = "superAdmin".equals(sessionManager.getUserRole());
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(category.getImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.ivCategoryImage);
        }

        holder.btnEditCategory.setOnClickListener(v -> {
            if (isSuperAdmin) {
                // TODO: Open Edit Dialog or Activity
                Toast.makeText(context, "Sửa danh mục: " + category.getName(), Toast.LENGTH_SHORT).show();
            } else {
                showPermissionDialog();
            }
        });

        holder.btnDeleteCategory.setOnClickListener(v -> {
            if (isSuperAdmin) {
                // TODO: Call Delete API
                Toast.makeText(context, "Xóa danh mục: " + category.getName(), Toast.LENGTH_SHORT).show();
            } else {
                showPermissionDialog();
            }
        });
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Yêu cầu quyền truy cập")
                .setMessage("Bạn chỉ có quyền xem. Vui lòng gửi yêu cầu cấp quyền lên Super Admin để chỉnh sửa.")
                .setPositiveButton("Gửi yêu cầu", (dialog, which) -> {
                    Toast.makeText(context, "Đã gửi yêu cầu đến Super Admin", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }

    public void updateData(List<Category> newList) {
        this.categoryList = newList;
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName;
        ImageButton btnEditCategory, btnDeleteCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            btnEditCategory = itemView.findViewById(R.id.btnEditCategory);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
