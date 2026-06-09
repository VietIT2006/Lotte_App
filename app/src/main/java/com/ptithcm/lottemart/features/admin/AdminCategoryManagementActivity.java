package com.ptithcm.lottemart.features.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Category;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryManagementActivity extends BaseAdminActivity {

    private FloatingActionButton fabAddCategory;
    private RecyclerView rvAdminCategories;
    private AdminCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category_management);

        setHeaderTitle("Quản lý Danh mục");
        

        fabAddCategory = findViewById(R.id.fabAddCategory);
        rvAdminCategories = findViewById(R.id.rvAdminCategories);
        
        rvAdminCategories.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AdminCategoryAdapter(this, new ArrayList<>());
        rvAdminCategories.setAdapter(adapter);

        SessionManager sessionManager = new SessionManager(this);
        boolean isSuperAdmin = "superAdmin".equalsIgnoreCase(sessionManager.getUserRole());

        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSuperAdmin) {
                    Intent intent = new Intent(AdminCategoryManagementActivity.this, AdminCategoryFormActivity.class);
                    startActivity(intent);
                } else {
                    showPermissionDialog();
                }
            }
        });

        fetchCategories();
    }

    private void fetchCategories() {
        ProductApiService apiService = RetrofitClient.getClient().create(ProductApiService.class);
        apiService.getCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Category> categories = response.body().getData();
                        adapter.updateData(categories);
                    } else {
                        Toast.makeText(AdminCategoryManagementActivity.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminCategoryManagementActivity.this, "Không thể lấy dữ liệu danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                Toast.makeText(AdminCategoryManagementActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
