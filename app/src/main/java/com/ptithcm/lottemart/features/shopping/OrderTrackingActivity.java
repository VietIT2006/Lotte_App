package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.MapApiService;
import com.ptithcm.lottemart.data.models.NominatimResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.remote.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Socket mSocket;
    private Marker shipperMarker;
    private Marker customerMarker;
    private LatLng customerLatLng;
    
    private TextView tvDriverName;
    private ImageButton btnCallDriver;
    private Button btnRateOrder;

    private String orderId = "1"; // Mock data, should get from Intent
    private String customerAddress = "Quận 1, TP HCM"; // Mock data

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
        btnRateOrder = findViewById(R.id.btnRateOrder);

        // Giả lập thông tin shipper
        tvDriverName.setText("Nguyễn Văn A");
        String shipperPhone = "0123456789";

        btnCallDriver.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + shipperPhone));
            startActivity(intent);
        });

        btnRateOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển sang màn hình Đánh giá", Toast.LENGTH_SHORT).show();
        });

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

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
                        
                        runOnUiThread(() -> updateShipperLocationOnMap(new LatLng(lat, lng)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void updateShipperLocationOnMap(LatLng latLng) {
        if (mMap == null) return;
        
        if (shipperMarker == null) {
            shipperMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Shipper đang ở đây")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        } else {
            shipperMarker.setPosition(latLng);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        geocodeCustomerAddress();
    }

    private void geocodeCustomerAddress() {
        MapApiService api = RetrofitClient.getMapClient().create(MapApiService.class);
        String url = "https://nominatim.openstreetmap.org/search";
        api.geocodeAddress(url, customerAddress, "json").enqueue(new Callback<List<NominatimResponse>>() {
            @Override
            public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    NominatimResponse loc = response.body().get(0);
                    LatLng customerLatLng = new LatLng(Double.parseDouble(loc.getLat()), Double.parseDouble(loc.getLon()));
                    
                    if (mMap != null) {
                        customerMarker = mMap.addMarker(new MarkerOptions()
                                .position(customerLatLng)
                                .title("Địa chỉ của bạn"));
                        
                        // Nếu chưa có shipper marker, zoom vào khách
                        if (shipperMarker == null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatLng, 14f));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                Toast.makeText(OrderTrackingActivity.this, "Lỗi Geocoding địa chỉ khách", Toast.LENGTH_SHORT).show();
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
