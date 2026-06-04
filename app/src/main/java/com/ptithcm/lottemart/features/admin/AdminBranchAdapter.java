package com.ptithcm.lottemart.features.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Branch;

import java.util.List;

public class AdminBranchAdapter extends RecyclerView.Adapter<AdminBranchAdapter.BranchViewHolder> {

    private Context context;
    private List<Branch> branchList;
    private boolean isSuperAdmin;

    public AdminBranchAdapter(Context context, List<Branch> branchList) {
        this.context = context;
        this.branchList = branchList;
        SessionManager sessionManager = new SessionManager(context);
        this.isSuperAdmin = "superAdmin".equals(sessionManager.getUserRole());
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_branch, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch branch = branchList.get(position);
        
        holder.tvBranchName.setText(branch.getName());
        holder.tvBranchAddress.setText(branch.getAddress());

        holder.btnEditBranch.setOnClickListener(v -> {
            if (isSuperAdmin) {
                // TODO: Open Edit Branch Activity/Dialog
                Toast.makeText(context, "Sửa chi nhánh: " + branch.getName(), Toast.LENGTH_SHORT).show();
            } else {
                showPermissionDialog();
            }
        });

        holder.btnDeleteBranch.setOnClickListener(v -> {
            if (isSuperAdmin) {
                // TODO: Call Delete API
                Toast.makeText(context, "Xóa chi nhánh: " + branch.getName(), Toast.LENGTH_SHORT).show();
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
        return branchList == null ? 0 : branchList.size();
    }

    public void updateData(List<Branch> newList) {
        this.branchList = newList;
        notifyDataSetChanged();
    }

    public static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView tvBranchName, tvBranchAddress;
        ImageButton btnEditBranch, btnDeleteBranch;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBranchName = itemView.findViewById(R.id.tvBranchName);
            tvBranchAddress = itemView.findViewById(R.id.tvBranchAddress);
            btnEditBranch = itemView.findViewById(R.id.btnEditBranch);
            btnDeleteBranch = itemView.findViewById(R.id.btnDeleteBranch);
        }
    }
}
