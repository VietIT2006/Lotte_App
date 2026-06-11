package com.ptithcm.lottemart.features.fogotPassword;
import com.ptithcm.lottemart.R;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.ptithcm.lottemart.data.api.AuthApiService;
import com.ptithcm.lottemart.data.api.ForgotPasswordRequest;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.api.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import com.ptithcm.lottemart.features.auth.OtpVerificationActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_forgot_password);

        authApiService = RetrofitClient.getClient().create(AuthApiService.class);

        TextInputEditText edtInput = findViewById(R.id.edtEmailPhone);
        MaterialButton btnSend = findViewById(R.id.btnSendCode);

        btnSend.setOnClickListener(v -> {
            String email = edtInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Email!", Toast.LENGTH_SHORT).show();
            } else {
                btnSend.setEnabled(false);
                btnSend.setText("Đang gửi...");

                authApiService.forgotPassword(new ForgotPasswordRequest(email)).enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        btnSend.setEnabled(true);
                        btnSend.setText("Gửi mã xác nhận");

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Mã 6 số đã được gửi tới email của bạn!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                            intent.putExtra("EMAIL", email);
                            startActivity(intent);
                        } else {
                            String msg = "Có lỗi xảy ra";
                            if (response.body() != null && response.body().getMessage() != null) {
                                msg = response.body().getMessage();
                            } else if (response.errorBody() != null) {
                                try {
                                    org.json.JSONObject jObjError = new org.json.JSONObject(response.errorBody().string());
                                    msg = jObjError.getString("message");
                                } catch (Exception e) {
                                    msg = "Lỗi phản hồi từ máy chủ";
                                }
                            }
                            Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        btnSend.setEnabled(true);
                        btnSend.setText("Gửi mã xác nhận");
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
