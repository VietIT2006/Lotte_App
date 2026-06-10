package com.ptithcm.lottemart.features.shipper;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.ptithcm.lottemart.R;

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
        mapWebView.setWebViewClient(new WebViewClient());

        // Location of delivery (Lotte Mart District 7 - 10.735165, 106.700149)
        double lat = 10.735165;
        double lon = 106.700149;
        String osmUrl = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=16/" + lat + "/" + lon;
        mapWebView.loadUrl(osmUrl);

        // Complete Order button click
        Button btnCompleteOrder = findViewById(R.id.btnCompleteOrder);
        btnCompleteOrder.setOnClickListener(v -> {
            Intent intent = new Intent(ShipperDeliveryDetailsActivity.this, ShipperProofOfDeliveryActivity.class);
            startActivity(intent);
        });

        // Start Delivery button click
        Button btnStartDelivery = findViewById(R.id.btnStartDelivery);
        btnStartDelivery.setOnClickListener(v -> {
            Intent intent = new Intent(ShipperDeliveryDetailsActivity.this, ShipperTrackingActivity.class);
            // Dummy test data
            intent.putExtra("ORDER_ID", "1");
            intent.putExtra("CUSTOMER_ADDRESS", "Quận 1, TP HCM");
            startActivity(intent);
        });
    }
}
