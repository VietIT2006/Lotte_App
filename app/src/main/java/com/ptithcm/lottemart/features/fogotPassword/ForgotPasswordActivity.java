package com.ptithcm.lottemart.features.fogotPassword;
import com.ptithcm.lottemart.R;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_forgot_password);

        TextInputEditText edtInput = findViewById(R.id.edtEmailPhone);
        MaterialButton btnSend = findViewById(R.id.btnSendCode);
        final String HARDCODED_EMAIL = "admin@lottemart.vn";

        btnSend.setOnClickListener(v -> {
            String userInput = edtInput.getText().toString().trim();

            if (userInput.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập thông tin!", Toast.LENGTH_SHORT).show();
            } else if (userInput.equals(HARDCODED_EMAIL)) {
                // Giả lập thành công
                Toast.makeText(this, "Mã code đã được gửi tới email của bạn!", Toast.LENGTH_LONG).show();
            } else {
                // Giả lập không tìm thấy tài khoản
                Toast.makeText(this, "Tài khoản không tồn tại trên hệ thống.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
