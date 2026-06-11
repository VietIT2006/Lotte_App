package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class PayosPaymentActivity extends AppCompatActivity {

    private WebView webView;
    private String paymentUrl;
    private String orderCode;
    private String orderId;
    private double totalAmount;
    private String customerAddress;
    private String type; // 'order' or 'wallet_topup'

    interface PayosConfirmService {
        @GET("payments/confirm/{orderCode}")
        Call<ApiResponse<Void>> confirmPayment(@Path("orderCode") String orderCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payos_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> handleCancel());

        paymentUrl = getIntent().getStringExtra("PAYMENT_URL");
        orderCode = getIntent().getStringExtra("ORDER_CODE");
        orderId = getIntent().getStringExtra("ORDER_ID");
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0);
        customerAddress = getIntent().getStringExtra("CUSTOMER_ADDRESS");
        type = getIntent().getStringExtra("TYPE");

        if (paymentUrl == null || paymentUrl.isEmpty()) {
            Toast.makeText(this, "Không có link thanh toán!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("LotteMartApp/1.0 (PTITHCM Student Project)");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https://lotte-app.local/payment-success")) {
                    handleSuccess();
                    return true;
                } else if (url.startsWith("https://lotte-app.local/payment-failure")) {
                    handleCancel();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (url.startsWith("https://lotte-app.local/payment-success")) {
                    handleSuccess();
                } else if (url.startsWith("https://lotte-app.local/payment-failure")) {
                    handleCancel();
                }
            }
        });

        webView.loadUrl(paymentUrl);
    }

    private void handleSuccess() {
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
        
        // Gọi API confirm lên local server để hoàn thành ngay (hữu ích cho localhost dev)
        if (orderCode != null && !orderCode.isEmpty()) {
            PayosConfirmService service = RetrofitClient.getClient().create(PayosConfirmService.class);
            service.confirmPayment(orderCode).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    redirectOnSuccess();
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    redirectOnSuccess();
                }
            });
        } else {
            redirectOnSuccess();
        }
    }

    private void redirectOnSuccess() {
        if ("order".equals(type)) {
            Intent intent = new Intent(this, PaymentSuccessActivity.class);
            intent.putExtra("ORDER_ID", orderId);
            intent.putExtra("TOTAL_AMOUNT", String.format("%,.0fđ", totalAmount));
            intent.putExtra("CUSTOMER_ADDRESS", customerAddress);
            startActivity(intent);
        } else {
            // wallet topup
            setResult(RESULT_OK);
        }
        finish();
    }

    private void handleCancel() {
        if (orderCode != null && !orderCode.isEmpty()) {
            Toast.makeText(this, "Đang kiểm tra trạng thái thanh toán...", Toast.LENGTH_SHORT).show();
            PayosConfirmService service = RetrofitClient.getClient().create(PayosConfirmService.class);
            service.confirmPayment(orderCode).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        handleSuccess();
                    } else {
                        executeCancel();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    executeCancel();
                }
            });
        } else {
            executeCancel();
        }
    }

    private void executeCancel() {
        Toast.makeText(this, "Thanh toán đã bị hủy!", Toast.LENGTH_SHORT).show();
        if ("order".equals(type)) {
            Intent intent = new Intent(this, PaymentFailureActivity.class);
            startActivity(intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            handleCancel();
        }
    }
}
