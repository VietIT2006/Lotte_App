package com.ptithcm.lottemart.features.shipper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.MapApiService;
import com.ptithcm.lottemart.data.models.NominatimResponse;
import com.ptithcm.lottemart.data.models.OsrmResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.remote.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.net.Uri;
import android.content.Intent;
import android.widget.Button;

public class ShipperTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Socket mSocket;
    private String orderId;
    private String customerAddress;
    
    private Marker shipperMarker;
    private Marker customerMarker;
    private Polyline routePolyline;
    private LatLng destinationLatLng;

    private TextView tvDestinationAddress;
    private TextView tvDistance;
    private Button btnOpenGmaps;
    private Button btnArrived;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        initSocket();
        geocodeCustomerAddress();
    }

    private void openGoogleMaps() {
        if (destinationLatLng != null) {
            String uri = "google.navigation:q=" + destinationLatLng.latitude + "," + destinationLatLng.longitude + "&mode=d";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Chưa cài đặt Google Maps", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Chưa có toạ độ điểm đến", Toast.LENGTH_SHORT).show();
        }
    }

    private void markAsArrived() {
        Toast.makeText(this, "Đã cập nhật trạng thái: Đã đến nơi", Toast.LENGTH_SHORT).show();
        // Cập nhật API trạng thái đơn hàng ở đây
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
                    destinationLatLng = new LatLng(Double.parseDouble(loc.getLat()), Double.parseDouble(loc.getLon()));
                    
                    if (mMap != null) {
                        customerMarker = mMap.addMarker(new MarkerOptions()
                                .position(destinationLatLng)
                                .title("Khách hàng"));
                    }
                } else {
                    Toast.makeText(ShipperTrackingActivity.this, "Không tìm thấy toạ độ khách hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                Toast.makeText(ShipperTrackingActivity.this, "Lỗi Geocoding", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
            mMap.setMyLocationEnabled(true);
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
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (shipperMarker == null) {
            shipperMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Bạn đang ở đây")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
        } else {
            shipperMarker.setPosition(currentLatLng);
        }

        // Gửi toạ độ lên server
        if (mSocket != null && mSocket.connected()) {
            try {
                JSONObject payload = new JSONObject();
                payload.put("orderId", orderId);
                payload.put("lat", location.getLatitude());
                payload.put("lng", location.getLongitude());
                mSocket.emit("update_location", payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Vẽ route nếu đã có điểm đến
        if (destinationLatLng != null) {
            drawRoute(currentLatLng, destinationLatLng);
        }
    }

    private void drawRoute(LatLng start, LatLng end) {
        // OSRM coordinates format: longitude,latitude
        String coords = start.longitude + "," + start.latitude + ";" + end.longitude + "," + end.latitude;
        String url = "http://router.project-osrm.org/route/v1/driving/" + coords + "?overview=full&geometries=geojson";

        MapApiService api = RetrofitClient.getMapClient().create(MapApiService.class);
        api.getRoute(url).enqueue(new Callback<OsrmResponse>() {
            @Override
            public void onResponse(Call<OsrmResponse> call, Response<OsrmResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getRoutes() != null && !response.body().getRoutes().isEmpty()) {
                    OsrmResponse.Route route = response.body().getRoutes().get(0);
                    tvDistance.setText(String.format("Khoảng cách: %.1f km", route.getDistance() / 1000.0));
                    
                    // Parsing geometry (GeoJSON LineString)
                    try {
                        JSONObject geometry = new JSONObject(route.getGeometry());
                        org.json.JSONArray coordinates = geometry.getJSONArray("coordinates");
                        List<LatLng> points = new ArrayList<>();
                        for (int i = 0; i < coordinates.length(); i++) {
                            org.json.JSONArray pt = coordinates.getJSONArray(i);
                            // GeoJSON is [lon, lat]
                            points.add(new LatLng(pt.getDouble(1), pt.getDouble(0)));
                        }

                        if (routePolyline != null) {
                            routePolyline.remove();
                        }
                        routePolyline = mMap.addPolyline(new PolylineOptions()
                                .addAll(points)
                                .width(10)
                                .color(Color.BLUE));
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
