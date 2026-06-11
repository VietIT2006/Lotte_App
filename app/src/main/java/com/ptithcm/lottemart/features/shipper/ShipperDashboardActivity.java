package com.ptithcm.lottemart.features.shipper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.DeliveryApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.ShipperOrder;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.features.auth.LoginActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwitchCompat switchStatus;
    private SwitchCompat switchAutoAccept;
    private TextView tvDriverWelcome;
    private TextView tvDriverSubtitle;
    private TextView tvWalletBalance;
    private TextView tvPendingCount;
    private TextView tvDeliveringCount;
    private TextView tvCompletedCount;
    private SessionManager sessionManager;
    private DeliveryApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_dashboard);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient().create(DeliveryApiService.class);

        // Bind Views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        switchStatus = findViewById(R.id.switchStatus);
        switchAutoAccept = findViewById(R.id.switchAutoAccept);
        tvDriverWelcome = findViewById(R.id.tvDriverWelcome);
        tvDriverSubtitle = findViewById(R.id.tvDriverSubtitle);
        tvWalletBalance = findViewById(R.id.tvWalletBalance);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvDeliveringCount = findViewById(R.id.tvDeliveringCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);

        // Set Toolbar Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Listen for Navigation Items
        navigationView.setNavigationItemSelectedListener(this);

        // Populate Driver details from SessionManager
        String fullName = sessionManager.getUserName();
        String userId = sessionManager.getUserId();
        
        tvDriverWelcome.setText("Xin chào, " + fullName + "!");
        tvDriverSubtitle.setText("Mã tài xế: " + (userId.length() > 6 ? userId.substring(userId.length() - 6) : userId));

        // Update Header Views
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView tvHeaderDriverName = headerView.findViewById(R.id.tvHeaderDriverName);
            if (tvHeaderDriverName != null) {
                tvHeaderDriverName.setText(fullName);
            }
        }

        // Action Buttons
        findViewById(R.id.btnViewOrders).setOnClickListener(v -> {
            Intent intent = new Intent(this, ShipperOrdersActivity.class);
            startActivity(intent);
        });

        // Status switch listeners
        android.content.SharedPreferences prefs = getSharedPreferences("ShipperPrefs", MODE_PRIVATE);

        switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("shipper_online", isChecked).apply();
            final String status = isChecked ? "online" : "offline";
            apiService.updateShipperStatus(new DeliveryApiService.UpdateStatusPayload(status))
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ShipperDashboardActivity.this, "Đã cập nhật trạng thái làm việc: " + status.toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ShipperDashboardActivity.this, "Lỗi cập nhật trạng thái lên server", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Toast.makeText(ShipperDashboardActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
        });

        switchAutoAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("auto_accept", isChecked).apply();
            if (isChecked) {
                Toast.makeText(this, "Đã bật tự động nhận đơn giao hàng mới", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đã tắt tự động nhận đơn giao hàng mới", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize state from SharedPreferences
        switchStatus.setChecked(prefs.getBoolean("shipper_online", true));
        switchAutoAccept.setChecked(prefs.getBoolean("auto_accept", false));

        // Fetch stats
        fetchOrdersStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchOrdersStats();
    }

    private void fetchOrdersStats() {
        if (apiService == null) return;
        
        // Load wallet balance dynamically
        apiService.getWalletInfo().enqueue(new Callback<ApiResponse<DeliveryApiService.WalletInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<DeliveryApiService.WalletInfo>> call, Response<ApiResponse<DeliveryApiService.WalletInfo>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DeliveryApiService.WalletInfo info = response.body().getData();
                    if (info != null) {
                        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                        tvWalletBalance.setText(format.format(info.balance));
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<DeliveryApiService.WalletInfo>> call, Throwable t) {}
        });

        // Load pending orders
        apiService.getShipperOrders("assigned").enqueue(new Callback<ApiResponse<List<ShipperOrder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShipperOrder>>> call, Response<ApiResponse<List<ShipperOrder>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ShipperOrder> list = response.body().getData();
                    tvPendingCount.setText(String.valueOf(list != null ? list.size() : 0));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<ShipperOrder>>> call, Throwable t) {}
        });

        // Load delivering orders
        apiService.getShipperOrders("delivering").enqueue(new Callback<ApiResponse<List<ShipperOrder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShipperOrder>>> call, Response<ApiResponse<List<ShipperOrder>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ShipperOrder> list = response.body().getData();
                    tvDeliveringCount.setText(String.valueOf(list != null ? list.size() : 0));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<ShipperOrder>>> call, Throwable t) {}
        });

        // Load completed orders
        apiService.getShipperOrders("completed").enqueue(new Callback<ApiResponse<List<ShipperOrder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShipperOrder>>> call, Response<ApiResponse<List<ShipperOrder>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ShipperOrder> list = response.body().getData();
                    tvCompletedCount.setText(String.valueOf(list != null ? list.size() : 0));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<ShipperOrder>>> call, Throwable t) {}
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_shipper_dashboard) {
            // Already here, just close drawer
        } else if (id == R.id.nav_shipper_orders) {
            Intent intent = new Intent(this, ShipperOrdersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_shipper_history) {
            Intent intent = new Intent(this, ShipperHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_shipper_wallet) {
            Intent intent = new Intent(this, ShipperWalletActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_shipper_notifications) {
            Intent intent = new Intent(this, com.ptithcm.lottemart.features.notifications.NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_shipper_help) {
            Toast.makeText(this, "Liên hệ tổng đài 1900 Lotte Mart để được trợ giúp", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_shipper_logout) {
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
