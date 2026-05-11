package com.ptithcm.lottemart.features.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.ptithcm.lottemart.MainActivity;
import com.ptithcm.lottemart.R;

public class OtpVerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_otp_verification);

        Button btnVerify = findViewById(R.id.btnVerifyOtp);
        if (btnVerify != null) {
            btnVerify.setOnClickListener(v -> {
                // Mock verification success -> Go to Main
                Intent intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }
}
