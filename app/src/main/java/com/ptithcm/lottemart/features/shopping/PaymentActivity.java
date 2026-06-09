package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;

public class PaymentActivity extends AppCompatActivity {
    private String orderId;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_payment);

        orderId = getIntent().getStringExtra("ORDER_ID");
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0);

        TextView tvPaymentTitle = findViewById(R.id.tvPaymentTitle);
        TextView tvPaymentAmount = findViewById(R.id.tvPaymentAmount);

        String paymentMethod = getIntent().getStringExtra("PAYMENT_METHOD");
        tvPaymentTitle.setText("Thanh toán qua " + (paymentMethod != null ? paymentMethod : "Cổng trực tuyến"));
        tvPaymentAmount.setText("Số tiền: " + String.format("%,.0fđ", totalAmount));

        MaterialButton btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnConfirmPayment.setOnClickListener(v -> {
            Toast.makeText(this, "Xác nhận chuyển khoản thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
            intent.putExtra("ORDER_ID", orderId);
            intent.putExtra("TOTAL_AMOUNT", String.format("%,.0fđ", totalAmount));
            intent.putExtra("CUSTOMER_ADDRESS", getIntent().getStringExtra("CUSTOMER_ADDRESS"));
            startActivity(intent);
            finish();
        });

        MaterialButton btnCancelPayment = findViewById(R.id.btnCancelPayment);
        btnCancelPayment.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, PaymentFailureActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
