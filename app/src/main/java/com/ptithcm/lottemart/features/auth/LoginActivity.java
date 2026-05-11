package com.ptithcm.lottemart.features.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.AuthApiService;
import com.ptithcm.lottemart.data.api.AuthResponseData;
import com.ptithcm.lottemart.data.api.LoginRequest;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.features.fogotPassword.ForgotPasswordActivity;
import com.ptithcm.lottemart.utils.Validator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private android.widget.Button btnLogin;
    private TextView tvForgotPassword, tvSignUp;
    private SessionManager sessionManager;
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.ptithcm.lottemart.data.remote.RetrofitClient.init(this);
        
        sessionManager = new SessionManager(this);
        authApiService = RetrofitClient.getClient().create(AuthApiService.class);
        
        // Kiểm tra Tự động đăng nhập
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.user_activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
            }
        });

        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isValid = true;

        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập Email của bạn");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        }

        if (!isValid) return;

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        LoginRequest loginRequest = new LoginRequest(email, password);
        authApiService.login(loginRequest).enqueue(new Callback<ApiResponse<AuthResponseData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponseData>> call, Response<ApiResponse<AuthResponseData>> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponseData data = response.body().getData();
                    sessionManager.saveAuthToken(
                            data.getToken(),
                            data.getUser().getFullName(),
                            data.getUser().getEmail()
                    );
                    
                    // CẬP NHẬT TOKEN VÀO HỆ THỐNG MẠNG NGAY LẬP TỨC
                    com.ptithcm.lottemart.data.remote.RetrofitClient.init(LoginActivity.this);
                    
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    String errorMsg = "Email hoặc mật khẩu không hợp lệ";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponseData>> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");
                Log.e(TAG, "Login failure", t);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, com.ptithcm.lottemart.MainActivity.class);
        startActivity(intent);
        finish();
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
