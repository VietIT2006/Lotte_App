package com.ptithcm.lottemart.features.shipper;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.ShipperOrder;

public class ShipperDeliveryDetailsActivity extends AppCompatActivity {

    private WebView mapWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_delivery_details);

        mapWebView = findViewById(R.id.mapWebView);

        // Configure WebView settings
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("LotteMartApp/1.0 (PTITHCM Student Project)");
        mapWebView.setWebViewClient(new WebViewClient());

        // Location of delivery (Lotte Mart District 7 - 10.735165, 106.700149)
        double lat = 10.735165;
        double lon = 106.700149;
        String osmUrl = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=16/" + lat + "/" + lon;
        mapWebView.loadUrl(osmUrl);

        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvDestination = findViewById(R.id.tvDestination);
        TextView tvCodTotal = findViewById(R.id.tvCodTotal);

        final ShipperOrder order = (ShipperOrder) getIntent().getSerializableExtra("SHIPPER_ORDER");
        final String finalOrderId = (order != null) ? order.getId() : "1";
        
        String address = "Quận 1, TP HCM";
        if (order != null) {
            String idStr = order.getId();
            String displayId = (idStr != null && idStr.length() >= 8) ? idStr.substring(0, 8) : (idStr != null ? idStr : "");
            tvOrderId.setText("Order #" + displayId);

            java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
            if (order.getPaymentMethod() != null && !"COD".equalsIgnoreCase(order.getPaymentMethod())) {
                tvCodTotal.setText("0đ (Đã thanh toán " + order.getPaymentMethod() + ")");
            } else if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                tvCodTotal.setText("0đ (Đã thanh toán)");
            } else {
                tvCodTotal.setText(format.format(order.getTotalAmount()) + " (Thu COD)");
            }

            Object addrObj = null;
            try {
                addrObj = order.getClass().getDeclaredField("order_address").get(order);
            } catch (Exception e) {}
            if (addrObj != null) {
                address = addrObj.toString();
                tvDestination.setText(address);
            }
        }
        final String finalCustomerAddress = address;

        // Complete Order button click
        Button btnCompleteOrder = findViewById(R.id.btnCompleteOrder);
        btnCompleteOrder.setVisibility(android.view.View.GONE);
        btnCompleteOrder.setOnClickListener(v -> {
            Intent intent = new Intent(ShipperDeliveryDetailsActivity.this, ShipperProofOfDeliveryActivity.class);
            intent.putExtra("ORDER_ID", finalOrderId);
            startActivity(intent);
        });

        // Start Delivery button click
        Button btnStartDelivery = findViewById(R.id.btnStartDelivery);
        
        // Adjust btnStartDelivery layout params to fill full width
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) btnStartDelivery.getLayoutParams();
        params.weight = 2;
        params.rightMargin = 0;
        btnStartDelivery.setLayoutParams(params);
        btnStartDelivery.setOnClickListener(v -> {
            Intent intent = new Intent(ShipperDeliveryDetailsActivity.this, ShipperTrackingActivity.class);
            intent.putExtra("ORDER_ID", finalOrderId);
            intent.putExtra("CUSTOMER_ADDRESS", finalCustomerAddress);
            startActivity(intent);
            finish(); // Finish details activity so it pops off the stack
        });
    }
}
