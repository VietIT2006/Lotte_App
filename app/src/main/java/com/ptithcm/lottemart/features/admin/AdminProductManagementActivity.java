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
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductManagementActivity extends BaseAdminActivity {

    private RecyclerView rvAdminProducts;
    private AdminProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_management);

        setHeaderTitle("Quản lý Sản phẩm");
        

        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        
        rvAdminProducts.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AdminProductAdapter(this, new ArrayList<>());
        rvAdminProducts.setAdapter(adapter);

        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        SessionManager sessionManager = new SessionManager(this);
        boolean isSuperAdmin = "superAdmin".equals(sessionManager.getUserRole());
        
        fabAddProduct.setOnClickListener(v -> {
            if (isSuperAdmin) {
                Intent intent = new Intent(AdminProductManagementActivity.this, AdminProductFormActivity.class);
                startActivity(intent);
            } else {
                showPermissionDialog();
            }
        });

        // Lấy danh sách sản phẩm (truyền null để lấy tất cả)
        fetchProducts();
    }

    private void fetchProducts() {
        ProductApiService apiService = RetrofitClient.getClient().create(ProductApiService.class);
        apiService.getProducts(null).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Product> products = response.body().getData();
                        adapter.updateData(products);
                    } else {
                        Toast.makeText(AdminProductManagementActivity.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminProductManagementActivity.this, "Không thể lấy dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                Toast.makeText(AdminProductManagementActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
