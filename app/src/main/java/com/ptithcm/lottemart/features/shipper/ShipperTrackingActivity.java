package com.ptithcm.lottemart.features.shipper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.appbar.MaterialToolbar;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.MapApiService;
import com.ptithcm.lottemart.data.models.NominatimResponse;
import com.ptithcm.lottemart.data.models.OsrmResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.remote.SocketManager;
import io.socket.client.Socket;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperTrackingActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private WebView mapWebView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Socket mSocket;
    private String orderId;
    private String customerAddress;
    
    private boolean isMapLoaded = false;
    private Double pendingShipperLat = null;
    private Double pendingShipperLng = null;
    private Double destinationLat = null;
    private Double destinationLng = null;
    private String pendingRouteJson = null;

    private TextView tvDestinationAddress;
    private TextView tvDistance;
    private Button btnOpenGmaps;
    private Button btnArrived;

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
            "        L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {\n" +
            "            maxZoom: 20,\n" +
            "            attribution: '© OpenStreetMap contributors © CARTO'\n" +
            "        }).addTo(map);\n" +
            "\n" +
            "        var customerMarker = null;\n" +
            "        var shipperMarker = null;\n" +
            "        var routePolyline = null;\n" +
            "\n" +
            "        function setCustomerLocation(lat, lon) {\n" +
            "            if (customerMarker) {\n" +
            "                map.removeLayer(customerMarker);\n" +
            "            }\n" +
            "            customerMarker = L.marker([lat, lon]).addTo(map).bindPopup('Khách hàng').openPopup();\n" +
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
            "            shipperMarker = L.marker([lat, lon], {icon: blueIcon}).addTo(map).bindPopup('Bạn đang ở đây');\n" +
            "        }\n" +
            "\n" +
            "        function drawRoute(pointsJson) {\n" +
            "            if (routePolyline) {\n" +
            "                map.removeLayer(routePolyline);\n" +
            "            }\n" +
            "            var points = JSON.parse(pointsJson);\n" +
            "            routePolyline = L.polyline(points, {color: 'blue', weight: 6}).addTo(map);\n" +
            "            var bounds = L.latLngBounds(points);\n" +
            "            map.fitBounds(bounds);\n" +
            "        }\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_tracking);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvDestinationAddress = findViewById(R.id.tvDestinationAddress);
        tvDistance = findViewById(R.id.tvDistance);
        btnOpenGmaps = findViewById(R.id.btnOpenGmaps);
        btnArrived = findViewById(R.id.btnArrived);
        mapWebView = findViewById(R.id.mapWebView);

        // Configure WebView settings
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("LotteMartApp/1.0 (PTITHCM Student Project)");
        mapWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isMapLoaded = true;
                
                if (destinationLat != null && destinationLng != null) {
                    setCustomerOnMap(destinationLat, destinationLng);
                }
                if (pendingShipperLat != null && pendingShipperLng != null) {
                    setShipperOnMap(pendingShipperLat, pendingShipperLng);
                }
                if (pendingRouteJson != null) {
                    drawRouteOnMap(pendingRouteJson);
                }
            }
        });

        mapWebView.loadDataWithBaseURL("https://openstreetmap.org", LEAFLET_HTML, "text/html", "UTF-8", null);

        btnOpenGmaps.setOnClickListener(v -> openGoogleMaps());
        btnArrived.setOnClickListener(v -> markAsArrived());

        orderId = getIntent().getStringExtra("ORDER_ID");
        customerAddress = getIntent().getStringExtra("CUSTOMER_ADDRESS");

        if (orderId == null || customerAddress == null) {
            Toast.makeText(this, "Thiếu thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDestinationAddress.setText(customerAddress);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initSocket();
        geocodeCustomerAddress();
        checkLocationPermission();
    }

    private void openGoogleMaps() {
        if (destinationLat != null && destinationLng != null) {
            String uri = "google.navigation:q=" + destinationLat + "," + destinationLng + "&mode=d";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + destinationLat + "," + destinationLng));
                startActivity(webIntent);
            }
        } else {
            Toast.makeText(this, "Chưa có toạ độ điểm đến", Toast.LENGTH_SHORT).show();
        }
    }

    private void markAsArrived() {
        Toast.makeText(this, "Đã cập nhật trạng thái: Đã đến nơi", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ShipperTrackingActivity.this, ShipperProofOfDeliveryActivity.class);
        intent.putExtra("ORDER_ID", orderId);
        startActivity(intent);
        finish();
    }

    private void initSocket() {
        SocketManager.connect();
        mSocket = SocketManager.getSocket();
        if (mSocket != null) {
            mSocket.emit("join_order", orderId);
        }
    }

    private void geocodeCustomerAddress() {
        MapApiService api = RetrofitClient.getMapClient().create(MapApiService.class);
        String url = "https://nominatim.openstreetmap.org/search";
        api.geocodeAddress(url, customerAddress, "json").enqueue(new Callback<List<NominatimResponse>>() {
            @Override
            public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    NominatimResponse loc = response.body().get(0);
                    destinationLat = Double.parseDouble(loc.getLat());
                    destinationLng = Double.parseDouble(loc.getLon());
                    
                    if (isMapLoaded) {
                        setCustomerOnMap(destinationLat, destinationLng);
                    }
                } else {
                    Toast.makeText(ShipperTrackingActivity.this, "Không tìm thấy toạ độ khách hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                Toast.makeText(ShipperTrackingActivity.this, "Lỗi định vị địa chỉ khách hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateDistanceMeters(10)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateShipperLocation(location);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void updateShipperLocation(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        if (!isMapLoaded) {
            pendingShipperLat = lat;
            pendingShipperLng = lng;
        } else {
            setShipperOnMap(lat, lng);
        }

        // Send to socket
        if (mSocket != null && mSocket.connected()) {
            try {
                JSONObject payload = new JSONObject();
                payload.put("orderId", orderId);
                payload.put("lat", lat);
                payload.put("lng", lng);
                mSocket.emit("update_location", payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Draw route
        if (destinationLat != null && destinationLng != null) {
            drawRoute(lat, lng, destinationLat, destinationLng);
        }
    }

    private void setShipperOnMap(double lat, double lng) {
        mapWebView.loadUrl("javascript:setShipperLocation(" + lat + "," + lng + ")");
    }

    private void setCustomerOnMap(double lat, double lng) {
        mapWebView.loadUrl("javascript:setCustomerLocation(" + lat + "," + lng + ")");
    }

    private void drawRouteOnMap(String pointsJson) {
        mapWebView.loadUrl("javascript:drawRoute('" + pointsJson + "')");
    }

    private void drawRoute(double startLat, double startLng, double endLat, double endLng) {
        String coords = startLng + "," + startLat + ";" + endLng + "," + endLat;
        String url = "https://router.project-osrm.org/route/v1/driving/" + coords + "?overview=full&geometries=geojson";

        MapApiService api = RetrofitClient.getMapClient().create(MapApiService.class);
        api.getRoute(url).enqueue(new Callback<OsrmResponse>() {
            @Override
            public void onResponse(Call<OsrmResponse> call, Response<OsrmResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getRoutes() != null && !response.body().getRoutes().isEmpty()) {
                    OsrmResponse.Route route = response.body().getRoutes().get(0);
                    tvDistance.setText(String.format("Khoảng cách: %.1f km", route.getDistance() / 1000.0));
                    
                    try {
                        JSONObject geometry = new JSONObject(route.getGeometry());
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        JSONArray points = new JSONArray();
                        for (int i = 0; i < coordinates.length(); i++) {
                            JSONArray pt = coordinates.getJSONArray(i);
                            // GeoJSON is [lon, lat], Leaflet is [lat, lon]
                            JSONArray point = new JSONArray();
                            point.put(pt.getDouble(1));
                            point.put(pt.getDouble(0));
                            points.put(point);
                        }

                        String pointsJson = points.toString();
                        if (!isMapLoaded) {
                            pendingRouteJson = pointsJson;
                        } else {
                            drawRouteOnMap(pointsJson);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OsrmResponse> call, Throwable t) {
                Log.e("OSRM", "Error getting route", t);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            } else {
                Toast.makeText(this, "Cần cấp quyền vị trí để theo dõi đường đi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (mSocket != null) {
            mSocket.emit("leave_order", orderId);
        }
    }
}
