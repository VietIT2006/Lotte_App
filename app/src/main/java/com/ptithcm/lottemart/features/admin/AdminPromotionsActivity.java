package com.ptithcm.lottemart.features.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.PromotionApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Promotion;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPromotionsActivity extends BaseAdminActivity {
    private RecyclerView rvList;
    private AdminPromotionAdapter adapter;
    private SessionManager sessionManager;
    private PromotionApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_promotions);

        setHeaderTitle("Quản lý Khuyến mãi");

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient().create(PromotionApiService.class);

        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminPromotionAdapter(this, new ArrayList<>(), new AdminPromotionAdapter.OnPromotionActionListener() {
            @Override
            public void onEdit(Promotion promotion) {
                showPromotionDialog(promotion);
            }

            @Override
            public void onDelete(Promotion promotion) {
                deletePromotion(promotion.getId());
            }
        });
        rvList.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> showPromotionDialog(null));

        fetchPromotions();
    }

    private void fetchPromotions() {
        String token = "Bearer " + sessionManager.getAuthToken();
        apiService.getAdminPromotions(token).enqueue(new Callback<ApiResponse<List<Promotion>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Promotion>>> call, Response<ApiResponse<List<Promotion>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setpromotions(response.body().getData());
                } else {
                    Toast.makeText(AdminPromotionsActivity.this, "Lỗi lấy danh sách Khuyến mãi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Promotion>>> call, Throwable t) {
                Toast.makeText(AdminPromotionsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPromotionDialog(Promotion existingPromotion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Tiêu đề (VD: Sale 50%)");
        layout.addView(etTitle);

        final EditText etDescription = new EditText(this);
        etDescription.setHint("Mô tả");
        layout.addView(etDescription);
        
        final EditText etType = new EditText(this);
        etType.setHint("Loại khuyến mãi (VD: discount, freeship)");
        layout.addView(etType);

        final EditText etImage = new EditText(this);
        etImage.setHint("URL Hình ảnh Banner");
        layout.addView(etImage);

        final CheckBox cbActive = new CheckBox(this);
        cbActive.setText("Đang hoạt động");
        cbActive.setChecked(true);
        layout.addView(cbActive);

        if (existingPromotion != null) {
            etTitle.setText(existingPromotion.getTitle());
            etDescription.setText(existingPromotion.getDescription());
            etType.setText(existingPromotion.getType());
            etImage.setText(existingPromotion.getBannerImage());
            cbActive.setChecked(existingPromotion.isActive());
        }

        builder.setView(layout);
        builder.setTitle(existingPromotion == null ? "Thêm Khuyến mãi" : "Sửa Khuyến mãi");

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String type = etType.getText().toString().trim();
            String image = etImage.getText().toString().trim();
            boolean isActive = cbActive.isChecked();

            if (title.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ Tiêu đề và Loại", Toast.LENGTH_SHORT).show();
                return;
            }

            Promotion promotion = new Promotion(title, description, image, type, isActive);
            
            if (existingPromotion == null) {
                createPromotion(promotion);
            } else {
                updatePromotion(existingPromotion.getId(), promotion);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void createPromotion(Promotion promotion) {
        String token = "Bearer " + sessionManager.getAuthToken();
        apiService.createPromotion(token, promotion).enqueue(new Callback<ApiResponse<Promotion>>() {
            @Override
            public void onResponse(Call<ApiResponse<Promotion>> call, Response<ApiResponse<Promotion>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminPromotionsActivity.this, "Tạo thành công", Toast.LENGTH_SHORT).show();
                    fetchPromotions();
                } else {
                    Toast.makeText(AdminPromotionsActivity.this, "Lỗi tạo Khuyến mãi", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Promotion>> call, Throwable t) {
                Toast.makeText(AdminPromotionsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePromotion(String id, Promotion promotion) {
        String token = "Bearer " + sessionManager.getAuthToken();
        apiService.updatePromotion(token, id, promotion).enqueue(new Callback<ApiResponse<Promotion>>() {
            @Override
            public void onResponse(Call<ApiResponse<Promotion>> call, Response<ApiResponse<Promotion>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminPromotionsActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    fetchPromotions();
                } else {
                    Toast.makeText(AdminPromotionsActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Promotion>> call, Throwable t) {
                Toast.makeText(AdminPromotionsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePromotion(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa khuyến mãi này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    String token = "Bearer " + sessionManager.getAuthToken();
                    apiService.deletePromotion(token, id).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(AdminPromotionsActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                fetchPromotions();
                            } else {
                                Toast.makeText(AdminPromotionsActivity.this, "Lỗi xóa Khuyến mãi", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Toast.makeText(AdminPromotionsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
        }
}
