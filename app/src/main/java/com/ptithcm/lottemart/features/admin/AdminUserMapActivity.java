package com.ptithcm.lottemart.features.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.MapApiService;
import com.ptithcm.lottemart.data.models.NominatimResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserMapActivity extends AppCompatActivity {

    private WebView mapWebView;
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
        mapWebView = findViewById(R.id.mapWebView);

        // Configure WebView settings for OpenStreetMap
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        mapWebView.setWebViewClient(new WebViewClient());

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

        ImageView btnOpenInGoogleMaps = findViewById(R.id.btnOpenInGoogleMaps);
        btnOpenInGoogleMaps.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(userAddress)));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(userAddress)));
                startActivity(webIntent);
            }
        });

        geocodeUserAddress();
    }

    private void geocodeUserAddress() {
        geocodeUserAddressWithFallback(userAddress, false);
    }

    private void geocodeUserAddressWithFallback(String address, boolean isFallback) {
        MapApiService api = RetrofitClient.getMapClient().create(MapApiService.class);
        String url = "https://nominatim.openstreetmap.org/search";
        api.geocodeAddress(url, address, "json").enqueue(new Callback<List<NominatimResponse>>() {
            @Override
            public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    NominatimResponse loc = response.body().get(0);
                    String lat = loc.getLat();
                    String lon = loc.getLon();
                    
                    // Construct a direct OSM marker view URL
                    String osmUrl = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=17/" + lat + "/" + lon;
                    mapWebView.loadUrl(osmUrl);
                } else if (!isFallback && address.contains(",")) {
                    int commaIdx = address.indexOf(",");
                    if (commaIdx != -1) {
                        String fallbackAddr = address.substring(commaIdx + 1).trim();
                        geocodeUserAddressWithFallback(fallbackAddr, true);
                    }
                } else {
                    Toast.makeText(AdminUserMapActivity.this, "Không tìm thấy toạ độ chính xác trên bản đồ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                if (!isFallback && address.contains(",")) {
                    int commaIdx = address.indexOf(",");
                    if (commaIdx != -1) {
                        String fallbackAddr = address.substring(commaIdx + 1).trim();
                        geocodeUserAddressWithFallback(fallbackAddr, true);
                        return;
                    }
                }
                Toast.makeText(AdminUserMapActivity.this, "Lỗi định vị địa chỉ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
