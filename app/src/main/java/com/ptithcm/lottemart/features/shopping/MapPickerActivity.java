package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.MapApiService;
import com.ptithcm.lottemart.data.models.NominatimResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPickerActivity extends AppCompatActivity {
    private static final String TAG = "MapPickerActivity";

    private WebView mapWebView;
    private TextView tvPinAddress;
    private MaterialButton btnConfirmPin;
    private String selectedStreet = "";
    private String selectedWard = "";
    private String selectedDistrict = "";
    private String selectedCity = "";
    private String selectedFullAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvPinAddress = findViewById(R.id.tvPinAddress);
        btnConfirmPin = findViewById(R.id.btnConfirmPin);

        btnConfirmPin.setEnabled(false);
        btnConfirmPin.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("street", selectedStreet);
            intent.putExtra("ward", selectedWard);
            intent.putExtra("district", selectedDistrict);
            intent.putExtra("city", selectedCity);
            intent.putExtra("full_address", selectedFullAddress);
            setResult(RESULT_OK, intent);
            finish();
        });

        // Initialize WebView Map Picker using 100% Free OpenStreetMap and Leaflet.js
        mapWebView = findViewById(R.id.mapWebView);
        mapWebView.getSettings().setJavaScriptEnabled(true);
        mapWebView.setWebViewClient(new WebViewClient());
        mapWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void onMapMoved(double lat, double lng) {
                runOnUiThread(() -> reverseGeocode(lat, lng));
            }
        }, "Android");

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\" />\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" />\n" +
                "    <style>\n" +
                "        body { padding: 0; margin: 0; }\n" +
                "        html, body, #map { height: 100%; width: 100vw; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n" +
                "    <script>\n" +
                "        var map = L.map('map', {zoomControl: false}).setView([10.752391, 106.697401], 16);\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            maxZoom: 19\n" +
                "        }).addTo(map);\n" +
                "\n" +
                "        map.on('moveend', function() {\n" +
                "            var center = map.getCenter();\n" +
                "            if (window.Android) {\n" +
                "                window.Android.onMapMoved(center.lat, center.lng);\n" +
                "            }\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
        
        mapWebView.loadData(htmlContent, "text/html", "UTF-8");
    }

    private void reverseGeocode(double lat, double lon) {
        tvPinAddress.setText("Đang giải mã tọa độ vị trí...");
        btnConfirmPin.setEnabled(false);

        MapApiService api = RetrofitClient.getMapClient().create(MapApiService.class);
        String url = "https://nominatim.openstreetmap.org/reverse";
        
        api.reverseGeocode(url, String.valueOf(lat), String.valueOf(lon), "json").enqueue(new Callback<NominatimResponse>() {
            @Override
            public void onResponse(Call<NominatimResponse> call, Response<NominatimResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NominatimResponse data = response.body();
                    String displayName = data.getDisplayName();
                    selectedFullAddress = displayName;
                    tvPinAddress.setText(displayName);
                    parseAddressComponents(displayName);
                    btnConfirmPin.setEnabled(true);
                } else {
                    tvPinAddress.setText("Không thể xác định địa chỉ tại tọa độ này.");
                }
            }

            @Override
            public void onFailure(Call<NominatimResponse> call, Throwable t) {
                Log.e(TAG, "Error reverse geocoding", t);
                tvPinAddress.setText("Lỗi kết nối mạng, di chuyển để thử lại.");
            }
        });
    }

    private void parseAddressComponents(String displayName) {
        String[] parts = displayName.split(",");
        selectedStreet = "";
        selectedWard = "";
        selectedDistrict = "";
        selectedCity = "";

        for (String part : parts) {
            part = part.trim();
            if (part.contains("Phường") || part.contains("Xã") || part.contains("phường") || part.contains("xã")) {
                selectedWard = part;
            } else if (part.contains("Quận") || part.contains("Huyện") || part.contains("quận") || part.contains("huyện")) {
                selectedDistrict = part;
            } else if (part.contains("Thành phố") || part.contains("Tỉnh") || part.contains("Thành Phố") || part.contains("Tỉnh Thừa Thiên Huế")) {
                selectedCity = part;
            }
        }

        // Street is generally the first 1 or 2 parts if they are not ward/district/city
        if (parts.length > 0) {
            String p0 = parts[0].trim();
            if (!p0.equalsIgnoreCase(selectedWard) && !p0.equalsIgnoreCase(selectedDistrict) && !p0.equalsIgnoreCase(selectedCity)) {
                selectedStreet = p0;
                if (parts.length > 1) {
                    String p1 = parts[1].trim();
                    if (!p1.equalsIgnoreCase(selectedWard) && !p1.equalsIgnoreCase(selectedDistrict) && !p1.equalsIgnoreCase(selectedCity)) {
                        selectedStreet = selectedStreet + ", " + p1;
                    }
                }
            }
        }
    }
}
