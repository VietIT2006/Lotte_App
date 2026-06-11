package com.ptithcm.lottemart.features.categories;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.features.shopping.ProductDetailActivity;
import com.ptithcm.lottemart.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryProductsActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private ProductApiService apiService;
    private String categoryId;
    private String categoryName;
    
    private int currentPage = 1;
    private int limit = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_category_products);

        categoryId = getIntent().getStringExtra("CATEGORY_ID");
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        TextView tvTitle = findViewById(R.id.tvCategoryTitle);
        tvTitle.setText(categoryName != null ? categoryName : "Sản phẩm");

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvProducts = findViewById(R.id.rvProducts);
        adapter = new ProductAdapter(this, new ArrayList<>(), product -> {
            Intent intent = new Intent(CategoryProductsActivity.this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            startActivity(intent);
        });
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(adapter);
        
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) { // Scrolling down
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if(layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                        if(!isLoading && !isLastPage) {
                            currentPage++;
                            fetchProducts();
                        }
                    }
                }
            }
        });

        apiService = RetrofitClient.getClient().create(ProductApiService.class);
        fetchProducts();
    }

    private void fetchProducts() {
        if (categoryId == null) return;
        
        isLoading = true;

        apiService.getProducts(categoryId, currentPage, limit).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> newProducts = response.body().getData();
                    ApiResponse.PaginationMeta pagination = response.body().getPagination();
                    
                    if (currentPage == 1) {
                        adapter.setProducts(newProducts);
                    } else {
                        adapter.addProducts(newProducts);
                    }
                    
                    if (pagination != null) {
                        isLastPage = currentPage >= pagination.getTotalPages();
                    } else {
                        isLastPage = newProducts == null || newProducts.isEmpty();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading = false;
                Toast.makeText(CategoryProductsActivity.this, "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
