package com.ptithcm.lottemart.features.loyalty;

import android.os.Bundle;
import android.widget.RadioGroup;
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

public class LPointTopupActivity extends AppCompatActivity {

    private RadioGroup rgTopupOptions;
    private MaterialButton btnSubmitTopup;
    private SessionManager sessionManager;
    private LoyaltyApiService loyaltyApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lpoint_topup);

        sessionManager = new SessionManager(this);
        loyaltyApiService = RetrofitClient.getClient().create(LoyaltyApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rgTopupOptions = findViewById(R.id.rgTopupOptions);
        btnSubmitTopup = findViewById(R.id.btnSubmitTopup);

        btnSubmitTopup.setOnClickListener(v -> executeTopup());
    }

    private void executeTopup() {
        int amount = 500;
        int checkedId = rgTopupOptions.getCheckedRadioButtonId();

        if (checkedId == R.id.rbOption1) {
            amount = 500;
        } else if (checkedId == R.id.rbOption2) {
            amount = 1000;
        } else if (checkedId == R.id.rbOption3) {
            amount = 5000;
        } else if (checkedId == R.id.rbOption4) {
            amount = 10000;
        }

        btnSubmitTopup.setEnabled(false);
        String token = "Bearer " + sessionManager.getAuthToken();
        LoyaltyApiService.TopupRequest request = new LoyaltyApiService.TopupRequest(amount);

        loyaltyApiService.topupPoints(token, request).enqueue(new Callback<ApiResponse<LoyaltyApiService.TopupResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoyaltyApiService.TopupResponse>> call, Response<ApiResponse<LoyaltyApiService.TopupResponse>> response) {
                btnSubmitTopup.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    int newPoints = response.body().getData().getNewPoints();
                    sessionManager.saveLottePoints(newPoints);
                    Toast.makeText(LPointTopupActivity.this, "Nạp điểm thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(LPointTopupActivity.this, "Nạp điểm thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoyaltyApiService.TopupResponse>> call, Throwable t) {
                btnSubmitTopup.setEnabled(true);
                Toast.makeText(LPointTopupActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
