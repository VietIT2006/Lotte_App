package com.ptithcm.lottemart.features.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.Supplier;

import java.util.List;

public class AdminSupplierAdapter extends RecyclerView.Adapter<AdminSupplierAdapter.ViewHolder> {
    private Context context;
    private List<Supplier> suppliers;

    public AdminSupplierAdapter(Context context, List<Supplier> suppliers) {
        this.context = context;
        this.suppliers = suppliers;
    }

    public void setsuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_supplier, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Supplier supplier = suppliers.get(position);
        holder.tvName.setText(supplier.getName() + " (" + supplier.getCode() + ")");
        holder.tvContact.setText("Liên hệ: " + supplier.getContactName());
        holder.tvPhone.setText("Phone: " + supplier.getPhone());
    }

    @Override
    public int getItemCount() {
        return suppliers != null ? suppliers.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvContact, tvPhone;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }
    }
}


