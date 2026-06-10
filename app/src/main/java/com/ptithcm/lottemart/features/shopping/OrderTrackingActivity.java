package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.MapApiService;
import com.ptithcm.lottemart.data.models.NominatimResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.remote.SocketManager;
import io.socket.client.Socket;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTrackingActivity extends AppCompatActivity {
    private WebView mapWebView;
    private Socket mSocket;
    
    private TextView tvDriverName;
    private ImageButton btnCallDriver;

    private String orderId = "1";
    private String customerAddress = "Quận 1, TP HCM";
    private boolean isMapLoaded = false;
    private Double pendingShipperLat = null;
    private Double pendingShipperLng = null;
    private Double customerLat = null;
    private Double customerLng = null;

    private static final String LEAFLET_HTML = 
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
            "    <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>\n" +
            "    <script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>\n" +
            "    <style>\n" +
            "        #map { height: 100vh; width: 100vw; margin: 0; padding: 0; }\n" +
            "        body { margin: 0; padding: 0; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div id='map'></div>\n" +
            "    <script>\n" +
            "        var map = L.map('map').setView([10.762622, 106.660172], 13);\n" +
            "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
            "            maxZoom: 19,\n" +
            "            attribution: '© OpenStreetMap'\n" +
            "        }).addTo(map);\n" +
            "\n" +
            "        var customerMarker = null;\n" +
            "        var shipperMarker = null;\n" +
            "\n" +
            "        function setCustomerLocation(lat, lon) {\n" +
            "            if (customerMarker) {\n" +
            "                map.removeLayer(customerMarker);\n" +
            "            }\n" +
            "            customerMarker = L.marker([lat, lon]).addTo(map).bindPopup('Địa chỉ của bạn').openPopup();\n" +
            "            map.setView([lat, lon], 14);\n" +
            "        }\n" +
            "\n" +
            "        function setShipperLocation(lat, lon) {\n" +
            "            if (shipperMarker) {\n" +
            "                map.removeLayer(shipperMarker);\n" +
            "            }\n" +
            "            var blueIcon = new L.Icon({\n" +
            "              iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',\n" +
            "              shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',\n" +
            "              iconSize: [25, 41],\n" +
            "              iconAnchor: [12, 41],\n" +
            "              popupAnchor: [1, -34],\n" +
            "              shadowSize: [41, 41]\n" +
            "            });\n" +
            "            shipperMarker = L.marker([lat, lon], {icon: blueIcon}).addTo(map).bindPopup('Shipper đang ở đây');\n" +
            "            map.setView([lat, lon], 15);\n" +
            "        }\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_order_tracking);

        if (getIntent().hasExtra("ORDER_ID")) {
            orderId = getIntent().getStringExtra("ORDER_ID");
        }
        if (getIntent().hasExtra("CUSTOMER_ADDRESS")) {
            customerAddress = getIntent().getStringExtra("CUSTOMER_ADDRESS");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        
        tvDriverName = findViewById(R.id.tvDriverName);
        btnCallDriver = findViewById(R.id.btnCallDriver);
        mapWebView = findViewById(R.id.mapWebView);

        // Configure WebView settings for OpenStreetMap Leaflet
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        mapWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isMapLoaded = true;
                
                // Load any pending coordinates
                if (customerLat != null && customerLng != null) {
                    setCustomerOnMap(customerLat, customerLng);
                }
                if (pendingShipperLat != null && pendingShipperLng != null) {
                    setShipperOnMap(pendingShipperLat, pendingShipperLng);
                }
            }
        });

        // Load the local interactive leaflet map
        mapWebView.loadDataWithBaseURL("https://openstreetmap.org", LEAFLET_HTML, "text/html", "UTF-8", null);

        tvDriverName.setText("Nguyễn Văn A");
        String shipperPhone = "0912345678";

        btnCallDriver.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + shipperPhone));
            startActivity(intent);
        });

        geocodeCustomerAddress();
        initSocket();
    }

    private void initSocket() {
        SocketManager.connect();
        mSocket = SocketManager.getSocket();
        
        if (mSocket != null) {
            mSocket.emit("join_order", orderId);

            mSocket.on("location_updated", args -> {
                if (args.length > 0) {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        double lat = data.getDouble("lat");
                        double lng = data.getDouble("lng");
                        
                        runOnUiThread(() -> updateShipperLocationOnMap(lat, lng));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void updateShipperLocationOnMap(double lat, double lng) {
        if (!isMapLoaded) {
            pendingShipperLat = lat;
            pendingShipperLng = lng;
            return;
        }
        setShipperOnMap(lat, lng);
    }

    private void setShipperOnMap(double lat, double lng) {
        mapWebView.loadUrl("javascript:setShipperLocation(" + lat + "," + lng + ")");
    }

    private void setCustomerOnMap(double lat, double lng) {
        mapWebView.loadUrl("javascript:setCustomerLocation(" + lat + "," + lng + ")");
    }

    private void geocodeCustomerAddress() {
        MapApiService api = RetrofitClient.getMapClient().create(MapApiService.class);
        String url = "https://nominatim.openstreetmap.org/search";
        api.geocodeAddress(url, customerAddress, "json").enqueue(new Callback<List<NominatimResponse>>() {
            @Override
            public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    NominatimResponse loc = response.body().get(0);
                    customerLat = Double.parseDouble(loc.getLat());
                    customerLng = Double.parseDouble(loc.getLon());
                    
                    if (isMapLoaded) {
                        setCustomerOnMap(customerLat, customerLng);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                Toast.makeText(OrderTrackingActivity.this, "Lỗi định vị địa chỉ khách hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.emit("leave_order", orderId);
            mSocket.off("location_updated");
        }
    }
}
