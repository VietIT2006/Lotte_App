package com.ptithcm.lottemart.features.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

public class AdminProductFormActivity extends BaseAdminActivity {

    private TextInputEditText edtProductName, edtProductPrice, edtProductOriginalPrice, edtProductDesc, edtProductImage;
    private ImageView ivProductPreview;
    private Button btnUploadImage, btnSave;
    private TextView tvFormTitle;
    
    private String currentImageUrl = "";
    private String productId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_form);

        setHeaderTitle("Biểu mẫu Sản phẩm");
        edtProductName = findViewById(R.id.edtProductName);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        edtProductOriginalPrice = findViewById(R.id.edtProductOriginalPrice);
        edtProductDesc = findViewById(R.id.edtProductDesc);
        edtProductImage = findViewById(R.id.edtProductImage);
        
        ivProductPreview = findViewById(R.id.ivProductPreview);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSave = findViewById(R.id.btnSave);
        tvFormTitle = findViewById(R.id.tvFormTitle);

        productId = getIntent().getStringExtra("PRODUCT_ID");
        if (productId != null) {
            tvFormTitle.setText("Sửa Sản phẩm");
            edtProductName.setText(getIntent().getStringExtra("PRODUCT_NAME"));
            edtProductPrice.setText(String.valueOf(getIntent().getDoubleExtra("PRODUCT_PRICE", 0)));
            edtProductOriginalPrice.setText(String.valueOf(getIntent().getDoubleExtra("PRODUCT_ORIGINAL_PRICE", 0)));
            edtProductDesc.setText(getIntent().getStringExtra("PRODUCT_DESC"));
            
            currentImageUrl = getIntent().getStringExtra("PRODUCT_IMAGE");
            if (currentImageUrl != null) {
                edtProductImage.setText(currentImageUrl);
            }
            Glide.with(this).load(currentImageUrl).into(ivProductPreview);
        }

        btnUploadImage.setOnClickListener(v -> mockUploadImage());
        
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void mockUploadImage() {
        Toast.makeText(this, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();
        currentImageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500";
        edtProductImage.setText(currentImageUrl);
        Glide.with(this).load(currentImageUrl).into(ivProductPreview);
        Toast.makeText(this, "Tải ảnh thành công!", Toast.LENGTH_SHORT).show();
    }

    private void saveProduct() {
        String name = edtProductName.getText().toString().trim();
        String priceStr = edtProductPrice.getText().toString().trim();
        String originalPriceStr = edtProductOriginalPrice.getText().toString().trim();
        String desc = edtProductDesc.getText().toString().trim();
        String imageUrlStr = edtProductImage.getText().toString().trim();
        if (!imageUrlStr.isEmpty()) {
            currentImageUrl = imageUrlStr;
        }

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và giá sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        double originalPrice = originalPriceStr.isEmpty() ? price : Double.parseDouble(originalPriceStr);

        Product product = new Product(productId, name, price, originalPrice, currentImageUrl, desc);
        ProductApiService apiService = RetrofitClient.getClient().create(ProductApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();
        
        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<Product>> callback = new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<Product>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<Product>> response) {
                btnSave.setEnabled(true);
                btnSave.setText("Lưu Sản phẩm");
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminProductFormActivity.this, "Lưu sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminProductFormActivity.this, "Lưu thất bại: " + (response.body() != null ? response.body().getMessage() : "Lỗi server"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<Product>> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Lưu Sản phẩm");
                Toast.makeText(AdminProductFormActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        if (productId == null || productId.isEmpty()) {
            apiService.addProduct(token, product).enqueue(callback);
        } else {
            apiService.updateProduct(token, productId, product).enqueue(callback);
        }
        }
}
