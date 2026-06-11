package com.ptithcm.lottemart.features.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.ptithcm.lottemart.MainActivity;
import com.ptithcm.lottemart.R;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;
import com.ptithcm.lottemart.features.fogotPassword.ResetPasswordActivity;

public class OtpVerificationActivity extends AppCompatActivity {
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_otp_verification);

        email = getIntent().getStringExtra("EMAIL");
        TextView tvDesc = findViewById(R.id.tvOtpDesc);
        if (email != null && tvDesc != null) {
            tvDesc.setText("Vui lòng nhập mã OTP đã được gửi đến " + email);
        }

        EditText[] otps = new EditText[]{
                findViewById(R.id.otp1),
                findViewById(R.id.otp2),
                findViewById(R.id.otp3),
                findViewById(R.id.otp4),
                findViewById(R.id.otp5),
                findViewById(R.id.otp6)
        };

        // Auto move focus to next edit text
        for (int i = 0; i < otps.length; i++) {
            final int currentIndex = i;
            otps[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < otps.length - 1) {
                        otps[currentIndex + 1].requestFocus();
                    } else if (s.length() == 0 && currentIndex > 0) {
                        otps[currentIndex - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        Button btnVerify = findViewById(R.id.btnVerifyOtp);
        if (btnVerify != null) {
            btnVerify.setOnClickListener(v -> {
                StringBuilder otpBuilder = new StringBuilder();
                for (EditText et : otps) {
                    otpBuilder.append(et.getText().toString());
                }
                
                String otpStr = otpBuilder.toString();
                if (otpStr.length() < 6) {
                    Toast.makeText(this, "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                intent.putExtra("EMAIL", email);
                intent.putExtra("OTP", otpStr);
                startActivity(intent);
            });
        }
    }
}
