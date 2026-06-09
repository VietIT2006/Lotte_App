package com.ptithcm.lottemart.features.loyalty;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.LoyaltyApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LPointActivity extends AppCompatActivity {
    private static final String TAG = "LPointActivity";
    private SessionManager sessionManager;
    private LoyaltyApiService loyaltyApiService;
    private TextView tvPointsBalance;
    private int currentPoints = 12450;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lpoint);

        sessionManager = new SessionManager(this);
        loyaltyApiService = RetrofitClient.getClient().create(LoyaltyApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvPointsBalance = findViewById(R.id.tvPointsBalance);
        
        // Lấy điểm offline tạm từ session trước
        currentPoints = sessionManager.getLottePoints();
        tvPointsBalance.setText(String.format("%,d P", currentPoints));

        // Sau đó tải điểm online từ Backend
        fetchPointsOnline();

        setupListeners();
    }

    private void fetchPointsOnline() {
        String token = "Bearer " + sessionManager.getAuthToken();
        loyaltyApiService.getPointsBalance(token).enqueue(new Callback<ApiResponse<LoyaltyApiService.PointsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoyaltyApiService.PointsResponse>> call, Response<ApiResponse<LoyaltyApiService.PointsResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentPoints = response.body().getData().getPoints();
                    sessionManager.saveLottePoints(currentPoints);
                    tvPointsBalance.setText(String.format("%,d P", currentPoints));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoyaltyApiService.PointsResponse>> call, Throwable t) {
                Log.e(TAG, "Lỗi tải điểm thưởng từ server, sử dụng điểm offline", t);
            }
        });
    }

    private void redeemPointsOnline(int points, String code, String title) {
        String token = "Bearer " + sessionManager.getAuthToken();
        LoyaltyApiService.RedeemRequest request = new LoyaltyApiService.RedeemRequest(points, code, title);
        
        loyaltyApiService.redeemPoints(token, request).enqueue(new Callback<ApiResponse<LoyaltyApiService.RedeemResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoyaltyApiService.RedeemResponse>> call, Response<ApiResponse<LoyaltyApiService.RedeemResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentPoints = response.body().getData().getNewPoints();
                    sessionManager.saveLottePoints(currentPoints);
                    tvPointsBalance.setText(String.format("%,d P", currentPoints));
                    Toast.makeText(LPointActivity.this, "Đổi điểm thành công! Voucher đã được lưu vào ví cá nhân của bạn.", Toast.LENGTH_LONG).show();
                } else {
                    String errorMsg = "Đổi điểm thất bại!";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.message();
                        } catch (Exception ignored) {}
                    }
                    Toast.makeText(LPointActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoyaltyApiService.RedeemResponse>> call, Throwable t) {
                Toast.makeText(LPointActivity.this, "Lỗi kết nối mạng, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        findViewById(R.id.btnTransfer).setOnClickListener(v -> 
            Toast.makeText(this, "Chức năng Chuyển điểm thành viên đang được xây dựng!", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.btnTopup).setOnClickListener(v -> 
            Toast.makeText(this, "Chức năng Nạp điểm thành viên đang được xây dựng!", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.btnHistory).setOnClickListener(v -> 
            Toast.makeText(this, "Chức năng xem Lịch sử điểm đang được xây dựng!", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.btnMyCard).setOnClickListener(v -> 
            Toast.makeText(this, "Thẻ thành viên của bạn đã được tích hợp hiển thị bên dưới!", Toast.LENGTH_SHORT).show()
        );

        MaterialButton btnRedeem1 = findViewById(R.id.btnRedeem1);
        btnRedeem1.setOnClickListener(v -> {
            if (currentPoints >= 2000) {
                redeemPointsOnline(2000, "MILK20K", "Voucher mua sữa giảm 20k");
            } else {
                Toast.makeText(this, "Bạn không đủ điểm L.POINT để đổi voucher này!", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialButton btnRedeem2 = findViewById(R.id.btnRedeem2);
        btnRedeem2.setOnClickListener(v -> {
            if (currentPoints >= 500) {
                redeemPointsOnline(500, "PARKINGFREE", "Voucher miễn phí gửi xe máy");
            } else {
                Toast.makeText(this, "Bạn không đủ điểm L.POINT để đổi voucher này!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
