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
import com.ptithcm.lottemart.data.api.OrderApiService;
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

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // XỬ LÝ CHỌN SỐ LƯỢNG
        TextView tvQty = findViewById(R.id.tvQuantityDetail);
        findViewById(R.id.btnPlusDetail).setOnClickListener(v -> {
            int q = Integer.parseInt(tvQty.getText().toString());
            tvQty.setText(String.valueOf(q + 1));
        });
        findViewById(R.id.btnMinusDetail).setOnClickListener(v -> {
            int q = Integer.parseInt(tvQty.getText().toString());
            if (q > 1) tvQty.setText(String.valueOf(q - 1));
        });

        findViewById(R.id.btnAddToCart).setOnClickListener(v -> {
            com.ptithcm.lottemart.data.local.SessionManager sessionManager = new com.ptithcm.lottemart.data.local.SessionManager(this);
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để thực hiện mua hàng", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, com.ptithcm.lottemart.features.auth.LoginActivity.class);
                startActivity(intent);
            } else {
                int qty = Integer.parseInt(tvQty.getText().toString());
                
                // Gửi API addToCart lên backend
                String token = "Bearer " + sessionManager.getAuthToken();
                OrderApiService orderApiService = RetrofitClient.getClient().create(OrderApiService.class);
                
                // Ở đây productId lấy từ Intent, ta cần đảm bảo có đầy đủ dữ liệu khi API detail trả về
                String pName = tvProductName.getText().toString();
                double pPrice = Double.parseDouble(tvPrice.getText().toString().replaceAll("[^0-9]", ""));
                
                // Tìm link ảnh nếu có
                String pImage = ""; 
                
                OrderApiService.AddToCartRequest req = new OrderApiService.AddToCartRequest(
                    productId,
                    pName,
                    pImage,
                    qty,
                    pPrice
                );
                
                orderApiService.addToCart(token, req).enqueue(new Callback<ApiResponse<OrderApiService.CartResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<OrderApiService.CartResponse>> call, Response<ApiResponse<OrderApiService.CartResponse>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ProductDetailActivity.this, "Đã thêm " + qty + " sản phẩm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Không thể thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<OrderApiService.CartResponse>> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối mạng, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void fetchProductDetails() {
        if (productId == null) return;

        apiService.getProductById(productId).enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<com.ptithcm.lottemart.data.models.Product>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<com.ptithcm.lottemart.data.models.Product>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<com.ptithcm.lottemart.data.models.Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    displayProduct(response.body().getData());
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<com.ptithcm.lottemart.data.models.Product>> call, Throwable t) {
                Log.e(TAG, "Error fetching product details", t);
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProduct(Product product) {
        tvProductName.setText(product.getName());
        tvPrice.setText(String.format("%,.0f đ", product.getPrice()));
        
        // Cập nhật Rating, Sold, Review Count
        TextView tvRating = findViewById(R.id.tvRating);
        if (tvRating != null) tvRating.setText(String.valueOf(product.getRating()));
        
        TextView tvReviewCount = findViewById(R.id.tvReviewCount);
        if (tvReviewCount != null) tvReviewCount.setText(String.format("(%d đánh giá)", product.getReviewCount()));
        
        TextView tvSoldCount = findViewById(R.id.tvSoldCount);
        if (tvSoldCount != null) tvSoldCount.setText(String.format("Đã bán: %d", product.getSoldCount()));
        
        // Brand, Origin, Unit
        TextView tvBrand = findViewById(R.id.tvBrand);
        if (tvBrand != null) tvBrand.setText(product.getBrand() != null && !product.getBrand().isEmpty() ? product.getBrand() : "Đang cập nhật");
        
        TextView tvOrigin = findViewById(R.id.tvOrigin);
        if (tvOrigin != null) tvOrigin.setText(product.getOrigin() != null && !product.getOrigin().isEmpty() ? product.getOrigin() : "Đang cập nhật");
        
        TextView tvUnit = findViewById(R.id.tvUnit);
        if (tvUnit != null) tvUnit.setText(product.getUnit() != null && !product.getUnit().isEmpty() ? product.getUnit() : "Đang cập nhật");
        
        // Highlights
        TextView tvHighlights = findViewById(R.id.tvHighlights);
        if (tvHighlights != null) {
            if (product.getHighlights() != null && !product.getHighlights().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String h : product.getHighlights()) {
                    sb.append("• ").append(h).append("\n");
                }
                tvHighlights.setText(sb.toString().trim());
            } else {
                tvHighlights.setText("Chưa có thông tin.");
            }
        }
        
        // Description
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvDescription.setText(product.getDescription());
        } else {
            tvDescription.setText("Sản phẩm chưa có mô tả.");
        }
        
        // Specifications
        android.widget.LinearLayout llSpecifications = findViewById(R.id.llSpecifications);
        if (llSpecifications != null) {
            llSpecifications.removeAllViews();
            if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
                for (Product.Specification spec : product.getSpecifications()) {
                    TextView tvSpec = new TextView(this);
                    tvSpec.setText("• " + spec.getLabel() + ": " + spec.getValue());
                    tvSpec.setTextColor(getResources().getColor(R.color.on_surface));
                    tvSpec.setPadding(0, 8, 0, 8);
                    llSpecifications.addView(tvSpec);
                }
            } else {
                TextView tvEmpty = new TextView(this);
                tvEmpty.setText("Chưa có thông số kỹ thuật.");
                llSpecifications.addView(tvEmpty);
            }
        }
        
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(com.ptithcm.lottemart.data.remote.NetworkConfig.getFullImageUrl(product.getImageUrl()))
                    .into(ivProductImage);
        }
    }
}
