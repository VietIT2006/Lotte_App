package com.ptithcm.lottemart.features.loyalty;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.LoyaltyApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LPointTransferActivity extends AppCompatActivity {

    private TextInputEditText etRecipient, etAmount;
    private MaterialButton btnSubmitTransfer;
    private SessionManager sessionManager;
    private LoyaltyApiService loyaltyApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lpoint_transfer);

        sessionManager = new SessionManager(this);
        loyaltyApiService = RetrofitClient.getClient().create(LoyaltyApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etRecipient = findViewById(R.id.etRecipient);
        etAmount = findViewById(R.id.etAmount);
        btnSubmitTransfer = findViewById(R.id.btnSubmitTransfer);

        btnSubmitTransfer.setOnClickListener(v -> executeTransfer());
    }

    private void executeTransfer() {
        String recipient = etRecipient.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (recipient.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thông tin người nhận", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điểm muốn chuyển", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "Số điểm phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount > sessionManager.getLottePoints()) {
            Toast.makeText(this, "Số dư điểm L.POINT của bạn không đủ!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitTransfer.setEnabled(false);
        String token = "Bearer " + sessionManager.getAuthToken();
        LoyaltyApiService.TransferRequest request = new LoyaltyApiService.TransferRequest(recipient, amount);

        loyaltyApiService.transferPoints(token, request).enqueue(new Callback<ApiResponse<LoyaltyApiService.TransferResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoyaltyApiService.TransferResponse>> call, Response<ApiResponse<LoyaltyApiService.TransferResponse>> response) {
                btnSubmitTransfer.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    int newPoints = response.body().getData().getNewPoints();
                    sessionManager.saveLottePoints(newPoints);
                    Toast.makeText(LPointTransferActivity.this, "Chuyển điểm thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String errorMsg = "Chuyển điểm thất bại!";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.message();
                        } catch (Exception ignored) {}
                    }
                    Toast.makeText(LPointTransferActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoyaltyApiService.TransferResponse>> call, Throwable t) {
                btnSubmitTransfer.setEnabled(true);
                Toast.makeText(LPointTransferActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
