package com.ptithcm.lottemart.features.shopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.Address;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private final Context context;
    private final List<Address> addresses;
    private final OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onSelect(Address address);
        void onDelete(Address address, int position);
    }

    public AddressAdapter(Context context, List<Address> addresses, OnAddressClickListener listener) {
        this.context = context;
        this.addresses = addresses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addresses.get(position);
        holder.tvLabel.setText(address.getLabel() != null ? address.getLabel().toUpperCase() : "HOME");
        holder.tvNamePhone.setText(address.getName() + " | " + address.getPhone());
        holder.tvAddressDetails.setText(address.getFullAddress());

        if (address.isDefault()) {
            holder.tvDefaultBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvDefaultBadge.setVisibility(View.GONE);
        }

        holder.btnSelect.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelect(address);
            }
        });

        holder.btnDeleteAddress.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(address, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvDefaultBadge, btnSelect, tvNamePhone, tvAddressDetails;
        android.widget.ImageView btnDeleteAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvDefaultBadge = itemView.findViewById(R.id.tvDefaultBadge);
            btnSelect = itemView.findViewById(R.id.btnSelect);
            tvNamePhone = itemView.findViewById(R.id.tvNamePhone);
            tvAddressDetails = itemView.findViewById(R.id.tvAddressDetails);
            btnDeleteAddress = itemView.findViewById(R.id.btnDeleteAddress);
        }
    }
}
