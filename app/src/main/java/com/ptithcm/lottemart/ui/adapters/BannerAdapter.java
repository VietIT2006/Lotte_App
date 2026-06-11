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
import com.ptithcm.lottemart.data.models.Banner;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
    private final Context context;
    private final List<Banner> banners;

    public BannerAdapter(Context context, List<Banner> banners) {
        this.context = context;
        this.banners = banners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Banner banner = banners.get(position);
        holder.tvBannerTitle.setText(banner.getTitle());
        holder.tvBannerSubtitle.setText(banner.getSummary());

        Glide.with(context)
            .load(com.ptithcm.lottemart.data.remote.NetworkConfig.getFullImageUrl(banner.getImageUrl()))
            .centerCrop()
            .into(holder.ivBannerImage);
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBannerImage;
        TextView tvBannerTitle, tvBannerSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBannerImage = itemView.findViewById(R.id.ivBannerImage);
            tvBannerTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvBannerSubtitle = itemView.findViewById(R.id.tvBannerSubtitle);
        }
    }
}
