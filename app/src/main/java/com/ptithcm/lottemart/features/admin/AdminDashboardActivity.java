package com.ptithcm.lottemart.features.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.models.Branch;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.features.auth.LoginActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        ImageView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> handleLogout());
        
        setupBottomNavigation();
        setupSidebarNavigation();
    }
    
    private void setupSidebarNavigation() {
        if (navView != null) {
            navView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_dashboard) {
                    // Do nothing, already here
                } else if (id == R.id.nav_inventory) {
                    startActivity(new Intent(this, AdminProductManagementActivity.class));
                } else if (id == R.id.nav_orders) {
                    startActivity(new Intent(this, AdminOrdersActivity.class));
                } else if (id == R.id.nav_customers) {
                    startActivity(new Intent(this, AdminUserManagementActivity.class));
                } else if (id == R.id.nav_promotions) {
                    startActivity(new Intent(this, AdminPromotionsActivity.class));
                } else if (id == R.id.nav_suppliers) {
                    startActivity(new Intent(this, AdminSuppliersActivity.class));
                } else if (id == R.id.nav_import_orders) {
                    startActivity(new Intent(this, AdminImportOrdersActivity.class));
                } else if (id == R.id.nav_receipts) {
                    startActivity(new Intent(this, AdminReceiptsActivity.class));
                } else if (id == R.id.nav_batches) {
                    startActivity(new Intent(this, AdminBatchesActivity.class));
                } else if (id == R.id.nav_transfers) {
                    startActivity(new Intent(this, AdminTransfersActivity.class));
                } else if (id == R.id.nav_roles) {
                    startActivity(new Intent(this, AdminRolesActivity.class));
                } else {
                    Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }
    
    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                return true;
            } else if (id == R.id.nav_products) {
                startActivity(new Intent(this, AdminProductManagementActivity.class));
                return false;
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(this, AdminCategoryManagementActivity.class));
                return false;
            } else if (id == R.id.nav_stores) {
                startActivity(new Intent(this, AdminBranchManagementActivity.class));
                return false;
            }
            return false;
        });
    }



    private void handleLogout() {
        sessionManager.logout();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
