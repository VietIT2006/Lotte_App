package com.ptithcm.lottemart.features.admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.User;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList = new ArrayList<>();
    private boolean isSuperAdmin;

    public AdminUserAdapter(Context context, boolean isSuperAdmin) {
        this.context = context;
        this.isSuperAdmin = isSuperAdmin;
    }

    public void setUsers(List<User> users) {
        this.userList = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        
        holder.tvUserName.setText(user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername());
        
        // Handle Role Badge
        String role = user.getRole();
        if ("superAdmin".equalsIgnoreCase(role) || "super_admin".equalsIgnoreCase(role)) {
            holder.tvUserRole.setText("Super Admin");
            holder.tvUserRole.setTextColor(Color.parseColor("#E53935")); // Red
            holder.tvUserRole.setBackgroundColor(Color.parseColor("#FFF0F0"));
        } else if ("admin".equalsIgnoreCase(role)) {
            holder.tvUserRole.setText("Admin");
            holder.tvUserRole.setTextColor(Color.parseColor("#FF9800")); // Orange
            holder.tvUserRole.setBackgroundColor(Color.parseColor("#FFF3E0"));
        } else {
            holder.tvUserRole.setText("Customer");
            holder.tvUserRole.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.tvUserRole.setBackgroundColor(Color.parseColor("#E8F5E9"));
        }

        // Handle Avatar
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.mipmap.ic_launcher_round)
                .circleCrop()
                .into(holder.ivUserAvatar);
        } else {
            holder.ivUserAvatar.setImageResource(R.mipmap.ic_launcher_round);
        }

        // Email & Phone
        String email = user.getEmail() != null ? user.getEmail() : "Chưa cập nhật email";
        String phone = user.getPhone() != null ? user.getPhone() : "Chưa cập nhật SĐT";
        holder.tvUserEmailPhone.setText(email + " - " + phone);

        // Address
        String address = user.getAddress() != null ? user.getAddress() : "Chưa cập nhật địa chỉ";
        holder.tvUserAddress.setText(address);

        // Stats (Points)
        if (!"admin".equalsIgnoreCase(role) && !"superAdmin".equalsIgnoreCase(role) && !"super_admin".equalsIgnoreCase(role)) {
            holder.tvStatLabel.setText("Lotte Points");
            holder.tvStatValue.setText(String.valueOf(user.getLottePoints()));
            
            holder.tvTaskValue.setText("Viewing App");
            holder.tvMembershipValue.setText(user.getMembershipLevel() != null ? user.getMembershipLevel() : "Thành viên");
        } else {
            holder.tvStatLabel.setText("Status");
            holder.tvStatValue.setText("Active");
            
            holder.tvTaskValue.setText("Managing System");
            holder.tvMembershipValue.setText("N/A");
        }

        // Logic ẩn/hiện Permissions Button
        if (isSuperAdmin) {
            holder.btnPermissions.setVisibility(View.VISIBLE);
            holder.btnPermissions.setOnClickListener(v -> {
                // TODO: Mở dialog cấp quyền/đổi role (gọi interface về Activity)
                Toast.makeText(context, "Mở Dialog phân quyền cho " + user.getUsername(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // Admin thường không được cấp quyền cho người khác
            holder.btnPermissions.setVisibility(View.GONE);
        }

        holder.btnMessage.setOnClickListener(v -> {
            Toast.makeText(context, "Nhắn tin cho " + user.getUsername(), Toast.LENGTH_SHORT).show();
        });

        holder.btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminUserMapActivity.class);
            intent.putExtra("USER_ADDRESS", "Quận 1, TP HCM");
            intent.putExtra("USER_NAME", user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername());
            intent.putExtra("USER_ROLE", role);
            intent.putExtra("USER_PHONE", user.getPhone() != null ? user.getPhone() : "Chưa cập nhật SĐT");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUserName, tvUserRole, tvStatLabel, tvStatValue;
        TextView tvTaskValue, tvMembershipValue;
        TextView tvUserEmailPhone, tvUserAddress;
        MaterialButton btnMessage, btnPermissions, btnMap;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvStatLabel = itemView.findViewById(R.id.tvStatLabel);
            tvStatValue = itemView.findViewById(R.id.tvStatValue);
            tvTaskValue = itemView.findViewById(R.id.tvTaskValue);
            tvMembershipValue = itemView.findViewById(R.id.tvMembershipValue);
            tvUserEmailPhone = itemView.findViewById(R.id.tvUserEmailPhone);
            tvUserAddress = itemView.findViewById(R.id.tvUserAddress);
            btnMessage = itemView.findViewById(R.id.btnMessage);
            btnPermissions = itemView.findViewById(R.id.btnPermissions);
            btnMap = itemView.findViewById(R.id.btnMap);
        }
    }
}
