package com.ptithcm.lottemart.features.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.Review;

import java.util.List;

public class AdminReviewAdapter extends RecyclerView.Adapter<AdminReviewAdapter.ViewHolder> {
    private Context context;
    private List<Review> reviews;
    private OnReviewActionListener listener;

    public interface OnReviewActionListener {
        void onDelete(Review review);
    }

    public AdminReviewAdapter(Context context, List<Review> reviews, OnReviewActionListener listener) {
        this.context = context;
        this.reviews = reviews;
        this.listener = listener;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        
        holder.tvUserName.setText(review.getUserName() != null ? review.getUserName() : "Khách hàng");
        holder.tvProductName.setText("products: " + (review.getProductName() != null ? review.getProductName() : "Unknown"));
        holder.tvRating.setText(review.getRating() + " ⭐");
        holder.tvComment.setText(review.getComment() != null ? review.getComment() : "");
        
        // Cắt bớt phần mili-giây nếu có
        String date = review.getCreatedAt();
        if (date != null && date.length() > 10) {
            date = date.substring(0, 10);
        }
        holder.tvDate.setText("Ngày: " + date);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(review);
        });
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvProductName, tvRating, tvComment, tvDate;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}


