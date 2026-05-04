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
import com.ptithcm.lottemart.data.api.RegisterRequest;
import com.ptithcm.lottemart.data.models.User;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.utils.Validator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private TextInputLayout tilFullName, tilRegisterEmail, tilRegisterPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etRegisterEmail, etRegisterPassword, etConfirmPassword;
    private android.widget.Button btnRegister;
    private TextView tvSignIn;
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_register);

        authApiService = RetrofitClient.getClient().create(AuthApiService.class);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        tilRegisterEmail = findViewById(R.id.tilRegisterEmail);
        tilRegisterPassword = findViewById(R.id.tilRegisterPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        
        etFullName = findViewById(R.id.etFullName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        
        btnRegister = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> handleRegistration());

        tvSignIn.setOnClickListener(v -> finish());

        etFullName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilFullName.setError(null);
            }
        });
        etRegisterEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilRegisterEmail.setError(null);
            }
        });
        etRegisterPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilRegisterPassword.setError(null);
            }
        });
        etConfirmPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilConfirmPassword.setError(null);
            }
        });
    }

    private void handleRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        if (fullName.isEmpty()) {
            tilFullName.setError("Họ tên không được để trống");
            isValid = false;
        }

        if (email.isEmpty()) {
            tilRegisterEmail.setError("Vui lòng nhập Email");
            isValid = false;
        } else if (!Validator.isEmailOrPhone(email)) {
            tilRegisterEmail.setError("Định dạng Email không hợp lệ");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilRegisterPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (!Validator.isValidPassword(password)) {
            tilRegisterPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            isValid = false;
        }

        if (!isValid) return;

        btnRegister.setEnabled(false);
        btnRegister.setText("Đang đăng ký...");

        // Use email as username
        RegisterRequest request = new RegisterRequest(email, email, "", password, fullName);
        authApiService.register(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Đăng ký");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
                    finish(); // Go back to login
                } else {
                    String errorMsg = "Đăng ký thất bại";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Đăng ký");
                Log.e(TAG, "Register failure", t);
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
