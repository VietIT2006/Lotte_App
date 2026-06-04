package com.ptithcm.lottemart.features.admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.User;

import java.util.List;

public class AdminRoleAdapter extends RecyclerView.Adapter<AdminRoleAdapter.ViewHolder> {
    private Context context;
    private List<User> users;
    private boolean isSuperAdmin;
    private OnRoleActionListener listener;

    public interface OnRoleActionListener {
        void onEdit(User user);
    }

    public AdminRoleAdapter(Context context, List<User> users, boolean isSuperAdmin, OnRoleActionListener listener) {
        this.context = context;
        this.users = users;
        this.isSuperAdmin = isSuperAdmin;
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_role, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        holder.tvUserName.setText(user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername());
        holder.tvUserEmail.setText(user.getEmail());
        
        String role = user.getRole() != null ? user.getRole() : "customer";
        holder.tvRole.setText(role.toUpperCase());

        if (role.equals("superAdmin")) {
            holder.tvRole.setBackgroundColor(Color.parseColor("#FFCDD2"));
            holder.tvRole.setTextColor(Color.parseColor("#D32F2F"));
        } else if (role.equals("admin")) {
            holder.tvRole.setBackgroundColor(Color.parseColor("#E3F2FD"));
            holder.tvRole.setTextColor(Color.parseColor("#1976D2"));
        } else {
            holder.tvRole.setBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.tvRole.setTextColor(Color.parseColor("#616161"));
        }

        if (isSuperAdmin) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(user);
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvRole;
        ImageView btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
