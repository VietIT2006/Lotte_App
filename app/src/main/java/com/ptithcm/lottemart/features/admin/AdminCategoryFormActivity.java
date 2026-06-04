package com.ptithcm.lottemart.features.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.models.Category;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryFormActivity extends BaseAdminActivity {

    private TextInputEditText edtCategoryName;
    private ImageView ivCategoryPreview;
    private Button btnUploadImage, btnSave;
    private TextView tvFormTitle;
    
    private String currentImageUrl = "";
    private String categoryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category_form);

        setHeaderTitle("Biểu mẫu Danh mục");
        edtCategoryName = findViewById(R.id.edtCategoryName);
        ivCategoryPreview = findViewById(R.id.ivCategoryPreview);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSave = findViewById(R.id.btnSave);
        tvFormTitle = findViewById(R.id.tvFormTitle);

        categoryId = getIntent().getStringExtra("CATEGORY_ID");
        if (categoryId != null) {
            tvFormTitle.setText("Sửa Danh mục");
            edtCategoryName.setText(getIntent().getStringExtra("CATEGORY_NAME"));
            currentImageUrl = getIntent().getStringExtra("CATEGORY_IMAGE");
            Glide.with(this).load(currentImageUrl).into(ivCategoryPreview);
        }

        btnUploadImage.setOnClickListener(v -> mockUploadImage());
        
        btnSave.setOnClickListener(v -> saveCategory());
    }

    private void mockUploadImage() {
        Toast.makeText(this, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();
        // Giả lập gọi API /upload (trong thực tế sẽ cần gửi Multipart FormData)
        // Hiện tại gán tạm URL tĩnh để demo
        currentImageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=200";
        Glide.with(this).load(currentImageUrl).into(ivCategoryPreview);
        Toast.makeText(this, "Tải ảnh thành công!", Toast.LENGTH_SHORT).show();
    }

    private void saveCategory() {
        String name = edtCategoryName.getText().toString().trim();
        if (name.isEmpty()) {
            edtCategoryName.setError("Vui lòng nhập tên danh mục");
            return;
        }

        Category cat = new Category(categoryId, name, currentImageUrl);
        ProductApiService apiService = RetrofitClient.getClient().create(ProductApiService.class);
        
        Toast.makeText(this, "Đang lưu...", Toast.LENGTH_SHORT).show();
        
        // Cần bổ sung logic POST / PUT trong ProductApiService để thực sự lưu
        // Vì mock nên chỉ đóng Activity lại
        Toast.makeText(this, "Lưu thành công (Mock)!", Toast.LENGTH_SHORT).show();
        finish();
        }
}
