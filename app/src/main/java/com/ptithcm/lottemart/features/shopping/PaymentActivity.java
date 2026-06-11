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
import com.ptithcm.lottemart.data.api.PayOSApiService;
import com.ptithcm.lottemart.utils.PayOSUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.OrderApiService;
import com.ptithcm.lottemart.data.models.Order;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.local.SessionManager;

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
            initPayOS();
        } else {
            initFallback();
        }
    }

    private void initPayOS() {
        progressBar.setVisibility(View.VISIBLE);
        layoutFallback.setVisibility(View.GONE);
        webViewPayment.setVisibility(View.GONE);

        webViewPayment.getSettings().setJavaScriptEnabled(true);
        webViewPayment.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("payment-success")) {
                    handlePaymentSuccess();
                    return true;
                } else if (url.contains("payment-cancel")) {
                    handlePaymentFailure();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-merchant.payos.vn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PayOSApiService apiService = retrofit.create(PayOSApiService.class);

        long orderCode = System.currentTimeMillis() / 1000;
        int amount = (int) totalAmount;
        if (amount <= 0) amount = 10000; // Mock safe amount
        String description = "LotteMart " + orderCode;
        String cancelUrl = "https://your-domain.com/payment-cancel";
        String returnUrl = "https://your-domain.com/payment-success";

        String signature = PayOSUtils.createSignature(orderCode, amount, description, cancelUrl, returnUrl, PayOSUtils.PAYOS_CHECKSUM_KEY);

        PayOSApiService.PayOSRequest request = new PayOSApiService.PayOSRequest(
                orderCode, amount, description, cancelUrl, returnUrl, signature
        );

        apiService.createPaymentLink(PayOSUtils.PAYOS_CLIENT_ID, PayOSUtils.PAYOS_API_KEY, request).enqueue(new Callback<PayOSApiService.PayOSResponse>() {
            @Override
            public void onResponse(Call<PayOSApiService.PayOSResponse> call, Response<PayOSApiService.PayOSResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && "00".equals(response.body().code) && response.body().data != null) {
                    webViewPayment.setVisibility(View.VISIBLE);
                    webViewPayment.loadUrl(response.body().data.checkoutUrl);
                } else {
                    Toast.makeText(PaymentActivity.this, "Lỗi tạo link thanh toán PayOS: " + (response.body() != null ? response.body().desc : "Unknown error"), Toast.LENGTH_LONG).show();
                    initFallback();
                }
            }

            @Override
            public void onFailure(Call<PayOSApiService.PayOSResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PaymentActivity.this, "Không thể kết nối PayOS", Toast.LENGTH_SHORT).show();
                initFallback();
            }
        });
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
