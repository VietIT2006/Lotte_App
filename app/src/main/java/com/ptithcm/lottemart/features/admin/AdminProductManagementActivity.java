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
    
    private int currentPage = 1;
    private int limit = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    
    private String currentQuery = "";
    private android.widget.EditText etAdminSearch;
    private android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_management);

        setHeaderTitle("Quản lý Sản phẩm");
        

        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        
        rvAdminProducts.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AdminProductAdapter(this, new ArrayList<>());
        rvAdminProducts.setAdapter(adapter);
        
        rvAdminProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if(layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                        if(!isLoading && !isLastPage) {
                            currentPage++;
                            fetchProducts();
                        }
                    }
                }
            }
        });

        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        SessionManager sessionManager = new SessionManager(this);
        String role = sessionManager.getUserRole();
        boolean isSuperAdmin = "superAdmin".equalsIgnoreCase(role) || "super_admin".equalsIgnoreCase(role);
        
        fabAddProduct.setOnClickListener(v -> {
            if (isSuperAdmin) {
                Intent intent = new Intent(AdminProductManagementActivity.this, AdminProductFormActivity.class);
                startActivity(intent);
            } else {
                showPermissionDialog();
            }
        });

        etAdminSearch = findViewById(R.id.etAdminSearch);
        etAdminSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    currentQuery = s.toString().trim();
                    currentPage = 1;
                    fetchProducts();
                };
                searchHandler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Lấy danh sách sản phẩm (truyền null để lấy tất cả)
        fetchProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentPage = 1;
        fetchProducts();
    }

    private void fetchProducts() {
        isLoading = true;
        ProductApiService apiService = RetrofitClient.getClient().create(ProductApiService.class);
        
        Call<ApiResponse<List<Product>>> call;
        if (currentQuery != null && !currentQuery.isEmpty()) {
            call = apiService.searchProducts(currentQuery, "latest", currentPage, limit);
        } else {
            call = apiService.getProducts(null, currentPage, limit);
        }
        
        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Product> products = response.body().getData();
                        ApiResponse.PaginationMeta pagination = response.body().getPagination();
                        
                        if (currentPage == 1) {
                            adapter.updateData(products);
                        } else {
                            adapter.addProducts(products);
                        }
                        
                        if (pagination != null) {
                            isLastPage = currentPage >= pagination.getTotalPages();
                        } else {
                            isLastPage = products == null || products.isEmpty();
                        }
                    } else {
                        Toast.makeText(AdminProductManagementActivity.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminProductManagementActivity.this, "Không thể lấy dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading = false;
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
