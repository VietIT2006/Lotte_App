package com.ptithcm.lottemart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ptithcm.lottemart.features.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private boolean isRetrofitReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemActiveIndicatorEnabled(false);
        bottomNav.setItemIconTintList(null);
        com.ptithcm.lottemart.data.local.SessionManager sessionManager = new com.ptithcm.lottemart.data.local.SessionManager(this);

        if (sessionManager.isLoggedIn() && ("admin".equalsIgnoreCase(sessionManager.getUserRole()) || "superAdmin".equalsIgnoreCase(sessionManager.getUserRole()))) {
            Intent intent = new Intent(this, com.ptithcm.lottemart.features.admin.AdminDashboardActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        // Đợi quét tìm IP backend thật trong mạng LAN xong mới nạp HomeFragment
        com.ptithcm.lottemart.data.remote.NetworkConfig.discoverBackendIP(this, () -> {
            runOnUiThread(() -> {
                // Reinit RetrofitClient với IP mới phát hiện được
                com.ptithcm.lottemart.data.remote.RetrofitClient.init(MainActivity.this);
                isRetrofitReady = true;

                // Ẩn màn hình loading
                android.view.View loadingView = findViewById(R.id.ipLoadingView);
                if (loadingView != null) {
                    loadingView.setVisibility(android.view.View.GONE);
                }

                // Nạp HomeFragment sau khi đã quét xong IP để đảm bảo call API thành công
                if (savedInstanceState == null) {
                    loadFragment(new HomeFragment());
                }
            });
        });

        bottomNav.setOnItemSelectedListener(item -> {
            if (!isRetrofitReady) return false; // Không cho phép chuyển tab khi chưa quét xong IP
            int itemId = item.getItemId();
            
            // Trang chủ và Danh mục cho phép xem không cần đăng nhập
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_categories) {
                loadFragment(new com.ptithcm.lottemart.features.categories.CategoriesFragment());
                return true;
            } else if (itemId == R.id.nav_voucher) {
                loadFragment(new com.ptithcm.lottemart.features.loyalty.LPointFragment());
                return true;
            }
            
            // CÁC TRANG CẦN ĐĂNG NHẬP
            if (!sessionManager.isLoggedIn()) {
                android.widget.Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng này", android.widget.Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, com.ptithcm.lottemart.features.auth.LoginActivity.class);
                startActivity(intent);
                return false; // Không chuyển tab nếu chưa đăng nhập
            }

            if (itemId == R.id.nav_cart) {
                loadFragment(new com.ptithcm.lottemart.features.shopping.CartFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new com.ptithcm.lottemart.features.home.ProfileFragment());
                return true;
            }
            return false;
        });
    }

    public void navigateToHome() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
    }
}