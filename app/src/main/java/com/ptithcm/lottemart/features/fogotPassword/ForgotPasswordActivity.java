package com.ptithcm.lottemart.features.fogotPassword;
import com.ptithcm.lottemart.R;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

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
        android.widget.Button btnSend = findViewById(R.id.btnSendCode);

        startAnimations();

        btnSend.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {
                    handleSendCode(edtInput, btnSend);
                }).start();
            }).start();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void startAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp1 = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation slideUp2 = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation slideUp3 = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        slideUp1.setStartOffset(150);
        slideUp2.setStartOffset(300);
        slideUp3.setStartOffset(450);

        findViewById(R.id.imgIcon).startAnimation(fadeIn);
        findViewById(R.id.tvTitle).startAnimation(fadeIn);
        findViewById(R.id.tvDesc).startAnimation(fadeIn);
        findViewById(R.id.inputLayout).startAnimation(slideUp1);
        findViewById(R.id.btnSendCode).startAnimation(slideUp2);
    }

    private void handleSendCode(TextInputEditText edtInput, android.widget.Button btnSend) {
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
                        showSuccessDialog("Mã 6 số đã được gửi tới email của bạn!", () -> {
                            Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                            intent.putExtra("EMAIL", email);
                            startActivity(intent);
                        });
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
    }

    private void showSuccessDialog(String message, Runnable onContinue) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_success);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);

        TextView tvMessage = dialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        dialog.show();

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (onContinue != null) {
                onContinue.run();
            }
        }, 2000);
    }
}
