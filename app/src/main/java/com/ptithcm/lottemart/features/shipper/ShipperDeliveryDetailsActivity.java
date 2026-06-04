package com.ptithcm.lottemart.features.shipper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ptithcm.lottemart.R;

public class ShipperDeliveryDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_delivery_details);

        // Khởi tạo Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Bắt sự kiện Complete Order -> chuyển sang trang chụp hình bằng chứng
        Button btnCompleteOrder = findViewById(R.id.btnCompleteOrder);
        btnCompleteOrder.setOnClickListener(v -> {
            Intent intent = new Intent(ShipperDeliveryDetailsActivity.this, ShipperProofOfDeliveryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Vị trí giao hàng (Ví dụ: Lotte Mart Quận 7)
        LatLng lotteQ7 = new LatLng(10.735165, 106.700149);
        mMap.addMarker(new MarkerOptions().position(lotteQ7).title("Lotte Mart Quận 7"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lotteQ7, 15f));
    }
}
