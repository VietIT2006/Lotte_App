package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.UserApiService;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private String orderId;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_payment);

        orderId = getIntent().getStringExtra("ORDER_ID");
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0);
        String paymentMethod = getIntent().getStringExtra("PAYMENT_METHOD");

        if ("PayOS".equalsIgnoreCase(paymentMethod)) {
            Toast.makeText(this, "Đang khởi tạo cổng thanh toán PayOS...", Toast.LENGTH_LONG).show();
            UserApiService userApiService = RetrofitClient.getClient().create(UserApiService.class);
            userApiService.createPayosLink(new UserApiService.PayosLinkRequest(orderId, totalAmount))
                .enqueue(new Callback<ApiResponse<UserApiService.PayosLinkResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<UserApiService.PayosLinkResponse>> call, Response<ApiResponse<UserApiService.PayosLinkResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            UserApiService.PayosLinkResponse data = response.body().getData();
                            Intent intent = new Intent(PaymentActivity.this, PayosPaymentActivity.class);
                            intent.putExtra("PAYMENT_URL", data.checkoutUrl);
                            intent.putExtra("ORDER_CODE", data.orderCode);
                            intent.putExtra("ORDER_ID", orderId);
                            intent.putExtra("TOTAL_AMOUNT", totalAmount);
                            intent.putExtra("CUSTOMER_ADDRESS", getIntent().getStringExtra("CUSTOMER_ADDRESS"));
                            intent.putExtra("TYPE", "order");
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(PaymentActivity.this, "Không tạo được liên kết thanh toán PayOS!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PaymentActivity.this, PaymentFailureActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<UserApiService.PayosLinkResponse>> call, Throwable t) {
                        Toast.makeText(PaymentActivity.this, "Lỗi kết nối PayOS: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PaymentActivity.this, PaymentFailureActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            return;
        }

        TextView tvPaymentTitle = findViewById(R.id.tvPaymentTitle);
        TextView tvPaymentAmount = findViewById(R.id.tvPaymentAmount);

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
