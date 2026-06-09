package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.lottemart.MainActivity;
import com.ptithcm.lottemart.R;

public class PaymentSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_payment_success);

        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvTotal = findViewById(R.id.tvTotal);
        Button btnGoHome = findViewById(R.id.btnGoHome);

        // Lấy dữ liệu từ intent (nếu có)
        String orderId = getIntent().getStringExtra("ORDER_ID");
        String total = getIntent().getStringExtra("TOTAL_AMOUNT");

        if (orderId != null) tvOrderId.setText("#" + orderId);
        if (total != null) tvTotal.setText(total);

        Button btnTrackOrder = findViewById(R.id.btnTrackOrder); // Thêm vào XML nếu cần
        if (btnTrackOrder != null) {
            btnTrackOrder.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrderTrackingActivity.class);
                intent.putExtra("ORDER_ID", orderId);
                intent.putExtra("CUSTOMER_ADDRESS", getIntent().getStringExtra("CUSTOMER_ADDRESS"));
                startActivity(intent);
                finish();
            });
        }

        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
