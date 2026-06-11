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

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
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

    private AutoCompleteTextView actvSearch;
    private FloatingActionButton fabMyLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private List<NominatimResponse> searchResults = new ArrayList<>();
    private ArrayAdapter<String> searchAdapter;

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
        actvSearch = findViewById(R.id.actvSearch);
        fabMyLocation = findViewById(R.id.fabMyLocation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupSearchAutocomplete();

        fabMyLocation.setOnClickListener(v -> requestLocationAndMoveMap());

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
        mapWebView.getSettings().setDomStorageEnabled(true);
        mapWebView.getSettings().setLoadsImagesAutomatically(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mapWebView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
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
                "        html, body, #map { height: 100%; width: 100%; }\n" +
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
                "        var geocodeTimer;\n" +
                "        map.on('moveend', function() {\n" +
                "            clearTimeout(geocodeTimer);\n" +
                "            geocodeTimer = setTimeout(function() {\n" +
                "                var center = map.getCenter();\n" +
                "                if (window.Android) {\n" +
                "                    window.Android.onMapMoved(center.lat, center.lng);\n" +
                "                }\n" +
                "            }, 1500);\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
        
        mapWebView.loadDataWithBaseURL("https://localhost/", htmlContent, "text/html", "UTF-8", null);

        // Tự động định vị ngay khi mở bản đồ
        requestLocationAndMoveMap();
    }

    private void reverseGeocode(double lat, double lon) {
        tvPinAddress.setText("Đang giải mã tọa độ vị trí...");
        btnConfirmPin.setEnabled(false);

        new Thread(() -> {
            try {
                android.location.Geocoder geocoder = new android.location.Geocoder(this, new java.util.Locale("vi", "VN"));
                java.util.List<android.location.Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    android.location.Address address = addresses.get(0);
                    
                    String fullAddress = address.getAddressLine(0);
                    
                    String street = address.getThoroughfare() != null ? address.getThoroughfare() : "";
                    String ward = address.getSubLocality() != null ? address.getSubLocality() : "";
                    String district = address.getSubAdminArea() != null ? address.getSubAdminArea() : "";
                    String city = address.getAdminArea() != null ? address.getAdminArea() : "";

                    // Combine house number and street
                    if (address.getFeatureName() != null && !address.getFeatureName().equals(street) && !address.getFeatureName().equals(ward)) {
                        street = address.getFeatureName() + (street.isEmpty() ? "" : " " + street);
                    }

                    // Fallback to array positions if any field is empty
                    if (fullAddress != null) {
                        String[] parts = fullAddress.split(",");
                        int len = parts.length;
                        
                        // ["97 Man Thiện", "Hiệp Phú", "Quận 9", "Hồ Chí Minh", "Việt Nam"]
                        if (city.isEmpty() && len >= 2) city = parts[len - 2].trim();
                        if (district.isEmpty() && len >= 3) district = parts[len - 3].trim();
                        if (ward.isEmpty() && len >= 4) ward = parts[len - 4].trim();
                        if (street.isEmpty() && len >= 5) {
                            street = parts[len - 5].trim();
                            // Sometimes street is split into two parts: ["97", "Man Thiện"]
                            if (len >= 6) street = parts[len - 6].trim() + " " + street;
                        }
                    }

                    final String finalStreet = street;
                    final String finalWard = ward;
                    final String finalDistrict = district;
                    final String finalCity = city;

                    runOnUiThread(() -> {
                        selectedFullAddress = fullAddress;
                        tvPinAddress.setText(fullAddress);
                        selectedStreet = finalStreet;
                        selectedWard = finalWard;
                        selectedDistrict = finalDistrict;
                        selectedCity = finalCity;
                        btnConfirmPin.setEnabled(true);
                    });
                } else {
                    runOnUiThread(() -> {
                        tvPinAddress.setText("Vị trí này không có tên đường cụ thể.");
                        selectedFullAddress = "Vị trí tùy chọn";
                        selectedStreet = "Vị trí trên bản đồ";
                        selectedWard = "";
                        selectedDistrict = "";
                        selectedCity = "";
                        btnConfirmPin.setEnabled(true);
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvPinAddress.setText("Vị trí này không có tên đường cụ thể.");
                    selectedFullAddress = "Vị trí tùy chọn";
                    selectedStreet = "Vị trí trên bản đồ";
                    selectedWard = "";
                    selectedDistrict = "";
                    selectedCity = "";
                    btnConfirmPin.setEnabled(true);
                });
            }
        }).start();
    }

    private void parseAddressComponents(String displayName) {
        // Obsolete: Now handled directly inside reverseGeocode using Geocoder fields and robust array parsing.
    }

    private void setupSearchAutocomplete() {
        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        actvSearch.setAdapter(searchAdapter);

        actvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    performSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        actvSearch.setOnItemClickListener((parent, view, position, id) -> {
            if (position < searchResults.size()) {
                NominatimResponse selected = searchResults.get(position);
                actvSearch.setText(selected.getDisplayName(), false);
                // Hide keyboard
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(actvSearch.getWindowToken(), 0);
                
                // Fly map to selected location
                String js = String.format("if (typeof map !== 'undefined') map.flyTo([%s, %s], 16);", selected.getLat(), selected.getLon());
                mapWebView.evaluateJavascript(js, null);
            }
        });
    }

    private void performSearch(String query) {
        MapApiService api = RetrofitClient.getMapClient().create(MapApiService.class);
        String url = "https://nominatim.openstreetmap.org/search";
        api.geocodeAddress(url, query, "json").enqueue(new Callback<List<NominatimResponse>>() {
            @Override
            public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResults = response.body();
                    List<String> displayNames = new ArrayList<>();
                    for (NominatimResponse r : searchResults) {
                        displayNames.add(r.getDisplayName());
                    }
                    searchAdapter.clear();
                    searchAdapter.addAll(displayNames);
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                Log.e(TAG, "Search autocomplete failed", t);
            }
        });
    }

    private void requestLocationAndMoveMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastKnownLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền vị trí để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Use getCurrentLocation instead of getLastLocation because getLastLocation can return null if there is no recent location cache
            fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        // Kiểm tra nếu là tọa độ mặc định của máy ảo Android (Mountain View, California)
                        // Vĩ độ khoảng 37.42, Kinh độ khoảng -122.08
                        if (Math.abs(lat - 37.422) < 0.05 && Math.abs(lng - (-122.084)) < 0.05) {
                            // Bay đến Lotte Mart Quận 7, TP.HCM để hiển thị bản đồ Việt Nam
                            lat = 10.741589;
                            lng = 106.701292;
                        }

                        String js = String.format("if (typeof map !== 'undefined') map.flyTo([%s, %s], 16);", lat, lng);
                        mapWebView.evaluateJavascript(js, null);
                    } else {
                        Toast.makeText(this, "Không thể lấy vị trí hiện tại. Hãy chắc chắn bạn đã bật GPS/Location trên máy.", Toast.LENGTH_LONG).show();
                    }
                });
        }
    }
}
