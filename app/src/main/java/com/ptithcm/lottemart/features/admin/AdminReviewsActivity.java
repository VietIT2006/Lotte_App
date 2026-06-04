package com.ptithcm.lottemart.features.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Review;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReviewsActivity extends BaseAdminActivity {
    private RecyclerView rvList;
    private AdminReviewAdapter adapter;
    private SessionManager sessionManager;
    private ProductApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reviews);

        setHeaderTitle("Quản lý Đánh giá");
        setHeaderTitle("Quản lý Đánh giá");
        setHeaderTitle("Qu?n l� Đánh giá");
        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient().create(ProductApiService.class);

        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminReviewAdapter(this, new ArrayList<>(), review -> {
            deleteReview(review.getId());
        });
        rvList.setAdapter(adapter);

        fetchReviews();
    }

    private void fetchReviews() {
        String token = "Bearer " + sessionManager.getAuthToken();
        apiService.getAdminReviews(token).enqueue(new Callback<ApiResponse<List<Review>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Review>>> call, Response<ApiResponse<List<Review>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setReviews(response.body().getData());
                } else {
                    Toast.makeText(AdminReviewsActivity.this, "Lỗi lấy danh sách đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Review>>> call, Throwable t) {
                Toast.makeText(AdminReviewsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReview(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa đánh giá này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    String token = "Bearer " + sessionManager.getAuthToken();
                    apiService.deleteReview(token, id).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(AdminReviewsActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                fetchReviews();
                            } else {
                                Toast.makeText(AdminReviewsActivity.this, "Lỗi xóa đánh giá", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Toast.makeText(AdminReviewsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
        }
}



