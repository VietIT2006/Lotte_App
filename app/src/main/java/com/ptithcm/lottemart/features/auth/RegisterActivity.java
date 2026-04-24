package com.ptithcm.lottemart.features.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.utils.Validator;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilRegisterEmail, tilRegisterPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etRegisterEmail, etRegisterPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvSignIn;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_register);

        sessionManager = new SessionManager(this);

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

        // Xóa lỗi khi người dùng nhập liệu
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
            tilRegisterEmail.setError("Vui lòng nhập Email hoặc Số điện thoại");
            isValid = false;
        } else if (!Validator.isEmailOrPhone(email)) {
            tilRegisterEmail.setError("Định dạng Email hoặc Số điện thoại không hợp lệ");
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

        // Giả lập lưu vào SessionManager cho Dynamic Mock Data
        sessionManager.registerMockUser(email, password, fullName);

        // Giả lập logic thành công, chuyển hướng sang màn hình OTP
        Toast.makeText(this, "Vui lòng nhập mã OTP để xác nhận", Toast.LENGTH_SHORT).show();
        android.content.Intent intent = new android.content.Intent(this, OtpVerificationActivity.class);
        startActivity(intent);
        finish();
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
