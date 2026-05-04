package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    private ImageView ivProductImage;
    private TextView tvProductName, tvPrice, tvDescription;
    private ProductApiService apiService;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_product_detail);

        productId = getIntent().getStringExtra("PRODUCT_ID");
        apiService = RetrofitClient.getClient().create(ProductApiService.class);

        initViews();
        fetchProductDetails();
    }

    private void initViews() {
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        findViewById(R.id.btnAddToCart).setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void fetchProductDetails() {
        if (productId == null) return;

        apiService.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    displayProduct(response.body().getData());
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                Log.e(TAG, "Error fetching product details", t);
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProduct(Product product) {
        tvProductName.setText(product.getName());
        tvPrice.setText(String.format("%,.0f đ", product.getPrice()));
        
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvDescription.setText(product.getDescription());
        } else {
            tvDescription.setText("Sản phẩm chưa có mô tả.");
        }
        
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrl())
                    .into(ivProductImage);
        }
    }
}
