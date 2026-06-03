package com.ptithcm.lottemart.features.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Branch;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminBranchManagementActivity extends AppCompatActivity {

    private FloatingActionButton fabAddBranch;
    private RecyclerView rvAdminBranches;
    private AdminBranchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_branch_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        fabAddBranch = findViewById(R.id.fabAddBranch);
        rvAdminBranches = findViewById(R.id.rvAdminBranches);
        
        rvAdminBranches.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AdminBranchAdapter(this, new ArrayList<>());
        rvAdminBranches.setAdapter(adapter);

        SessionManager sessionManager = new SessionManager(this);
        boolean isSuperAdmin = "superAdmin".equals(sessionManager.getUserRole());

        fabAddBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSuperAdmin) {
                    Toast.makeText(AdminBranchManagementActivity.this, "Chức năng thêm chi nhánh đang phát triển", Toast.LENGTH_SHORT).show();
                } else {
                    showPermissionDialog();
                }
            }
        });

        fetchBranches();
    }

    private void fetchBranches() {
        ProductApiService apiService = RetrofitClient.getClient().create(ProductApiService.class);
        apiService.getBranches().enqueue(new Callback<ApiResponse<List<Branch>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Branch>>> call, Response<ApiResponse<List<Branch>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Branch> branches = response.body().getData();
                        adapter.updateData(branches);
                    } else {
                        Toast.makeText(AdminBranchManagementActivity.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminBranchManagementActivity.this, "Không thể lấy dữ liệu chi nhánh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Branch>>> call, Throwable t) {
                Toast.makeText(AdminBranchManagementActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu quyền truy cập")
                .setMessage("Bạn chỉ có quyền xem. Vui lòng gửi yêu cầu cấp quyền lên Super Admin để thêm mới.")
                .setPositiveButton("Gửi yêu cầu", (dialog, which) -> {
                    Toast.makeText(this, "Đã gửi yêu cầu đến Super Admin", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
