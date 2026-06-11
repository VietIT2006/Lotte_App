package com.ptithcm.lottemart.features.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultsActivity";
    private TextView tvQuery, btnSortDefault, btnSortPrice;
    private ImageButton btnBack;
    private RecyclerView rvResults;
    private ProductAdapter productAdapter;
    private ProductApiService apiService;
    private String currentQuery;
    private String currentSort = "latest";
    
    private int currentPage = 1;
    private int limit = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_search_results);

        currentQuery = getIntent().getStringExtra("QUERY");
        apiService = RetrofitClient.getClient().create(ProductApiService.class);

        initViews();
        setupListeners();
        performSearch();
    }

    private void initViews() {
        tvQuery = findViewById(R.id.tvQuery);
        btnBack = findViewById(R.id.btnBack);
        btnSortDefault = findViewById(R.id.btnSortDefault);
        btnSortPrice = findViewById(R.id.btnSortPrice);
        rvResults = findViewById(R.id.rvResults);

        tvQuery.setText("Kết quả cho '" + currentQuery + "'");

        productAdapter = new ProductAdapter(this, new ArrayList<>(), product -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            startActivity(intent);
        });

        rvResults.setLayoutManager(new GridLayoutManager(this, 2));
        rvResults.setAdapter(productAdapter);
        
        rvResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) { // Scrolling down
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if(layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == productAdapter.getItemCount() - 1) {
                        if(!isLoading && !isLastPage) {
                            currentPage++;
                            performSearch();
                        }
                    }
                }
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSortDefault.setOnClickListener(v -> {
            currentSort = "latest";
            currentPage = 1;
            isLastPage = false;
            updateSortUI();
            performSearch();
        });

        btnSortPrice.setOnClickListener(v -> {
            currentSort = "price_asc";
            currentPage = 1;
            isLastPage = false;
            updateSortUI();
            performSearch();
        });
    }

    private void updateSortUI() {
        if (currentSort.equals("latest")) {
            btnSortDefault.setTextColor(ContextCompat.getColor(this, R.color.primary));
            btnSortDefault.setBackgroundResource(R.drawable.bg_sort_active);
            btnSortPrice.setTextColor(ContextCompat.getColor(this, R.color.grey_text));
            btnSortPrice.setBackground(null);
        } else {
            btnSortPrice.setTextColor(ContextCompat.getColor(this, R.color.primary));
            btnSortPrice.setBackgroundResource(R.drawable.bg_sort_active);
            btnSortDefault.setTextColor(ContextCompat.getColor(this, R.color.grey_text));
            btnSortDefault.setBackground(null);
        }
    }

    private void performSearch() {
        isLoading = true;
        apiService.searchProducts(currentQuery, currentSort, currentPage, limit).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData();
                    ApiResponse.PaginationMeta pagination = response.body().getPagination();
                    
                    if (currentPage == 1) {
                        productAdapter.setProducts(products);
                        if (products.isEmpty()) {
                            Toast.makeText(SearchResultsActivity.this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        productAdapter.addProducts(products);
                    }
                    
                    if (pagination != null) {
                        isLastPage = currentPage >= pagination.getTotalPages();
                    } else {
                        isLastPage = products == null || products.isEmpty();
                    }
                } else {
                    Log.e(TAG, "Search failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading = false;
                Log.e(TAG, "Search error", t);
                Toast.makeText(SearchResultsActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
