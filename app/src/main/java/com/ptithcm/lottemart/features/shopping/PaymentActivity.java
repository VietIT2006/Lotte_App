package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.UserApiService;
import com.ptithcm.lottemart.data.api.OrderApiService;
import com.ptithcm.lottemart.data.models.Order;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.local.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private String orderId;
    private double totalAmount;
    private String paymentMethod;
    private String customerAddress;

    private WebView webViewPayment;
    private ProgressBar progressBar;
    private LinearLayout layoutFallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_payment);

        orderId = getIntent().getStringExtra("ORDER_ID");
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0);
        paymentMethod = getIntent().getStringExtra("PAYMENT_METHOD");
        customerAddress = getIntent().getStringExtra("CUSTOMER_ADDRESS");

        webViewPayment = findViewById(R.id.webViewPayment);
        progressBar = findViewById(R.id.progressBar);
        layoutFallback = findViewById(R.id.layoutFallback);

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
                            intent.putExtra("CUSTOMER_ADDRESS", customerAddress);
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
        } else {
            initFallback();
        }
    }

    private void handlePaymentSuccess() {
        if (orderId != null && !orderId.isEmpty() && !orderId.equals("#LOTTE-MOCK")) {
            SessionManager sessionManager = new SessionManager(this);
            OrderApiService orderApiService = RetrofitClient.getClient().create(OrderApiService.class);
            String token = "Bearer " + sessionManager.getAuthToken();
            
            orderApiService.markOrderAsPaid(token, orderId).enqueue(new Callback<ApiResponse<Order>>() {
                @Override
                public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                    proceedToSuccessScreen();
                }

                @Override
                public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                    proceedToSuccessScreen();
                }
            });
        } else {
            proceedToSuccessScreen();
        }
    }

    private void proceedToSuccessScreen() {
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
        intent.putExtra("ORDER_ID", orderId);
        intent.putExtra("TOTAL_AMOUNT", String.format("%,.0fđ", totalAmount));
        intent.putExtra("CUSTOMER_ADDRESS", customerAddress);
        startActivity(intent);
        finish();
    }

    private void handlePaymentFailure() {
        Intent intent = new Intent(PaymentActivity.this, PaymentFailureActivity.class);
        startActivity(intent);
        finish();
    }

    private void initFallback() {
        progressBar.setVisibility(View.GONE);
        webViewPayment.setVisibility(View.GONE);
        layoutFallback.setVisibility(View.VISIBLE);

        TextView tvPaymentTitle = findViewById(R.id.tvPaymentTitle);
        TextView tvPaymentAmount = findViewById(R.id.tvPaymentAmount);

        tvPaymentTitle.setText("Thanh toán qua " + (paymentMethod != null ? paymentMethod : "Cổng trực tuyến"));
        tvPaymentAmount.setText("Số tiền: " + String.format("%,.0fđ", totalAmount));

        MaterialButton btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnConfirmPayment.setOnClickListener(v -> handlePaymentSuccess());

        MaterialButton btnCancelPayment = findViewById(R.id.btnCancelPayment);
        btnCancelPayment.setOnClickListener(v -> handlePaymentFailure());
    }
}
