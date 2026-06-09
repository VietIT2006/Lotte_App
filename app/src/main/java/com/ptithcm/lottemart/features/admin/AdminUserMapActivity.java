package com.ptithcm.lottemart.features.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.MapApiService;
import com.ptithcm.lottemart.data.models.NominatimResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

public class AdminUserMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String userAddress;
    private String userName;
    private String userPhone;
    private String userRole;
    private TextView tvUserAddress;
    private TextView tvUserName;
    private TextView tvUserRole;
    private ImageView btnCallUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_map);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvUserAddress = findViewById(R.id.tvUserAddress);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserRole = findViewById(R.id.tvUserRole);
        btnCallUser = findViewById(R.id.btnCallUser);

        userAddress = getIntent().getStringExtra("USER_ADDRESS");
        userName = getIntent().getStringExtra("USER_NAME");
        userRole = getIntent().getStringExtra("USER_ROLE");
        userPhone = getIntent().getStringExtra("USER_PHONE");

        if (userAddress == null || userAddress.isEmpty()) {
            Toast.makeText(this, "Không có địa chỉ để hiển thị", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvUserAddress.setText(userAddress);
        tvUserName.setText(userName != null ? userName : "Không rõ");
        tvUserRole.setText(userRole != null ? userRole : "Customer");

        btnCallUser.setOnClickListener(v -> {
            if (userPhone != null && !userPhone.isEmpty() && !userPhone.equals("Chưa cập nhật SĐT")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userPhone));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không có số điện thoại", Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        geocodeUserAddress();
    }

    private void geocodeUserAddress() {
        MapApiService api = RetrofitClient.getMapClient().create(MapApiService.class);
        String url = "https://nominatim.openstreetmap.org/search";
        api.geocodeAddress(url, userAddress, "json").enqueue(new Callback<List<NominatimResponse>>() {
            @Override
            public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    NominatimResponse loc = response.body().get(0);
                    LatLng userLatLng = new LatLng(Double.parseDouble(loc.getLat()), Double.parseDouble(loc.getLon()));
                    
                    if (mMap != null) {
                        mMap.addMarker(new MarkerOptions()
                                .position(userLatLng)
                                .title("Vị trí"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f));
                    }
                } else {
                    Toast.makeText(AdminUserMapActivity.this, "Không tìm thấy toạ độ cho địa chỉ này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                Toast.makeText(AdminUserMapActivity.this, "Lỗi Geocoding", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
