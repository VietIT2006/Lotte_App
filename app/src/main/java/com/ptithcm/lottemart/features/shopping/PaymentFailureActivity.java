package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.MainActivity;
import com.ptithcm.lottemart.R;

public class PaymentFailureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_payment_failure);

        MaterialButton btnRetryPayment = findViewById(R.id.btnRetryPayment);
        btnRetryPayment.setOnClickListener(v -> finish()); // Quay lại màn hình thanh toán trước đó

        MaterialButton btnBackToCart = findViewById(R.id.btnBackToCart);
        btnBackToCart.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentFailureActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
