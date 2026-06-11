package com.ptithcm.lottemart.features.fogotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.AuthApiService;
import com.ptithcm.lottemart.data.api.ResetPasswordRequest;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.features.auth.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private AuthApiService authApiService;
    private String email;
    private String otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_reset_password);

        email = getIntent().getStringExtra("EMAIL");
        otp = getIntent().getStringExtra("OTP");
        authApiService = RetrofitClient.getClient().create(AuthApiService.class);

        TextInputLayout tilNewPassword = findViewById(R.id.tilNewPassword);
        TextInputLayout tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        TextInputEditText edtNewPassword = findViewById(R.id.edtNewPassword);
        TextInputEditText edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(v -> {
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            boolean isValid = true;
            if (newPassword.isEmpty() || newPassword.length() < 6) {
                tilNewPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
                isValid = false;
            } else {
                tilNewPassword.setError(null);
            }

            if (!newPassword.equals(confirmPassword)) {
                tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                isValid = false;
            } else {
                tilConfirmPassword.setError(null);
            }

            if (!isValid) return;

            btnResetPassword.setEnabled(false);
            btnResetPassword.setText("Đang cập nhật...");

            ResetPasswordRequest request = new ResetPasswordRequest(email, otp, newPassword);
            authApiService.resetPassword(request).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    btnResetPassword.setEnabled(true);
                    btnResetPassword.setText("Cập nhật mật khẩu");

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
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
                        Toast.makeText(ResetPasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    btnResetPassword.setEnabled(true);
                    btnResetPassword.setText("Cập nhật mật khẩu");
                    Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
