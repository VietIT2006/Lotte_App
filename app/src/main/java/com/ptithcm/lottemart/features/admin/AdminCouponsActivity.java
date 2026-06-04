package com.ptithcm.lottemart.features.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.PromotionApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Coupon;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCouponsActivity extends BaseAdminActivity {
    private RecyclerView rvList;
    private AdminCouponAdapter adapter;
    private SessionManager sessionManager;
    private PromotionApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_coupons);

        setHeaderTitle("Quản lý");
        setHeaderTitle("Quản lý Coupons");
        setHeaderTitle("Qu?n l� Coupons");
        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient().create(PromotionApiService.class);

        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminCouponAdapter(this, new ArrayList<>(), new AdminCouponAdapter.OnCouponActionListener() {
            @Override
            public void onEdit(Coupon coupon) {
                showCouponDialog(coupon);
            }

            @Override
            public void onDelete(Coupon coupon) {
                deleteCoupon(coupon.getId());
            }
        });
        rvList.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> showCouponDialog(null));

        fetchCoupons();
    }

    private void fetchCoupons() {
        String token = "Bearer " + sessionManager.getAuthToken();
        apiService.getAdminCoupons(token).enqueue(new Callback<ApiResponse<List<Coupon>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Coupon>>> call, Response<ApiResponse<List<Coupon>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setCoupons(response.body().getData());
                } else {
                    Toast.makeText(AdminCouponsActivity.this, "Lỗi lấy danh sách Coupon", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Coupon>>> call, Throwable t) {
                Toast.makeText(AdminCouponsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCouponDialog(Coupon existingCoupon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etCode = new EditText(this);
        etCode.setHint("Mã Coupon (VD: SUMMER2024)");
        layout.addView(etCode);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Tiêu đề (VD: Giảm giá hè)");
        layout.addView(etTitle);
        
        final EditText etDiscount = new EditText(this);
        etDiscount.setHint("Giá trị giảm (VND)");
        etDiscount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etDiscount);

        final EditText etImage = new EditText(this);
        etImage.setHint("URL Hình ảnh");
        layout.addView(etImage);

        final CheckBox cbActive = new CheckBox(this);
        cbActive.setText("Đang hoạt động");
        cbActive.setChecked(true);
        layout.addView(cbActive);

        if (existingCoupon != null) {
            etCode.setText(existingCoupon.getCode());
            etTitle.setText(existingCoupon.getTitle());
            etDiscount.setText(String.valueOf((int)existingCoupon.getDiscountValue()));
            etImage.setText(existingCoupon.getImage());
            cbActive.setChecked(existingCoupon.isActive());
        }

        builder.setView(layout);
        builder.setTitle(existingCoupon == null ? "Thêm Coupon" : "Sửa Coupon");

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String code = etCode.getText().toString().trim();
            String title = etTitle.getText().toString().trim();
            String discountStr = etDiscount.getText().toString().trim();
            String image = etImage.getText().toString().trim();
            boolean isActive = cbActive.isChecked();

            if (code.isEmpty() || title.isEmpty() || discountStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ Mã, Tiêu đề và Giá trị giảm", Toast.LENGTH_SHORT).show();
                return;
            }

            double discountValue = Double.parseDouble(discountStr);
            Coupon coupon = new Coupon(code, title, discountValue, image, isActive);
            
            if (existingCoupon == null) {
                createCoupon(coupon);
            } else {
                updateCoupon(existingCoupon.getId(), coupon);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void createCoupon(Coupon coupon) {
        String token = "Bearer " + sessionManager.getAuthToken();
        apiService.createCoupon(token, coupon).enqueue(new Callback<ApiResponse<Coupon>>() {
            @Override
            public void onResponse(Call<ApiResponse<Coupon>> call, Response<ApiResponse<Coupon>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminCouponsActivity.this, "Tạo thành công", Toast.LENGTH_SHORT).show();
                    fetchCoupons();
                } else {
                    Toast.makeText(AdminCouponsActivity.this, "Lỗi tạo Coupon", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Coupon>> call, Throwable t) {
                Toast.makeText(AdminCouponsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCoupon(String id, Coupon coupon) {
        String token = "Bearer " + sessionManager.getAuthToken();
        apiService.updateCoupon(token, id, coupon).enqueue(new Callback<ApiResponse<Coupon>>() {
            @Override
            public void onResponse(Call<ApiResponse<Coupon>> call, Response<ApiResponse<Coupon>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminCouponsActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    fetchCoupons();
                } else {
                    Toast.makeText(AdminCouponsActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Coupon>> call, Throwable t) {
                Toast.makeText(AdminCouponsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCoupon(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa coupon này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    String token = "Bearer " + sessionManager.getAuthToken();
                    apiService.deleteCoupon(token, id).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(AdminCouponsActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                fetchCoupons();
                            } else {
                                Toast.makeText(AdminCouponsActivity.this, "Lỗi xóa Coupon", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Toast.makeText(AdminCouponsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
        }
}



