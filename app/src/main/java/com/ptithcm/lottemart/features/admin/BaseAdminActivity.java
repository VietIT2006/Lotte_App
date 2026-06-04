package com.ptithcm.lottemart.features.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.features.auth.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class BaseAdminActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navView;
    protected TextView tvHeaderTitle;
    protected TextView tvHeaderSubtitle;
    protected SessionManager sessionManager;

    @Override
    public void setContentView(int layoutResID) {
        // Inflate the base layout
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.layout_admin_base, null);
        
        // Find the content frame and inflate the child layout into it
        FrameLayout contentFrame = drawerLayout.findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, contentFrame, true);

        // Set the DrawerLayout as the root view for the activity
        super.setContentView(drawerLayout);

        // Initialize Base UI
        initBaseUI();
    }

    private void initBaseUI() {
        sessionManager = new SessionManager(this);
        navView = findViewById(R.id.nav_view);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);

        // Set today's date as default subtitle
        String currentDate = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(new Date());
        tvHeaderSubtitle.setText(currentDate);

        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        ImageView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> handleLogout());

        setupSidebarNavigation();
    }

    protected void setHeaderTitle(String title, String subtitle) {
        if (tvHeaderTitle != null) tvHeaderTitle.setText(title);
        if (tvHeaderSubtitle != null && subtitle != null) tvHeaderSubtitle.setText(subtitle);
    }
    
    protected void setHeaderTitle(String title) {
        if (tvHeaderTitle != null) tvHeaderTitle.setText(title);
    }

    private void setupSidebarNavigation() {
        if (navView != null) {
            navView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                Intent intent = null;

                if (id == R.id.nav_dashboard) {
                    if (!(this instanceof AdminDashboardActivity)) {
                        intent = new Intent(this, AdminDashboardActivity.class);
                    }
                } else if (id == R.id.nav_inventory) {
                    if (!(this instanceof AdminProductManagementActivity)) {
                        intent = new Intent(this, AdminProductManagementActivity.class);
                    }
                } else if (id == R.id.nav_categories) {
                    if (!(this instanceof AdminCategoryManagementActivity)) {
                        intent = new Intent(this, AdminCategoryManagementActivity.class);
                    }
                } else if (id == R.id.nav_stores) {
                    if (!(this instanceof AdminBranchManagementActivity)) {
                        intent = new Intent(this, AdminBranchManagementActivity.class);
                    }
                } else if (id == R.id.nav_orders) {
                    if (!(this instanceof AdminOrdersActivity)) {
                        intent = new Intent(this, AdminOrdersActivity.class);
                    }
                } else if (id == R.id.nav_customers) {
                    if (!(this instanceof AdminUserManagementActivity)) {
                        intent = new Intent(this, AdminUserManagementActivity.class);
                    }
                } else if (id == R.id.nav_promotions) {
                    if (!(this instanceof AdminPromotionsActivity)) {
                        intent = new Intent(this, AdminPromotionsActivity.class);
                    }
                } else if (id == R.id.nav_suppliers) {
                    if (!(this instanceof AdminSuppliersActivity)) {
                        intent = new Intent(this, AdminSuppliersActivity.class);
                    }
                } else if (id == R.id.nav_import_orders) {
                    if (!(this instanceof AdminImportOrdersActivity)) {
                        intent = new Intent(this, AdminImportOrdersActivity.class);
                    }
                } else if (id == R.id.nav_receipts) {
                    if (!(this instanceof AdminReceiptsActivity)) {
                        intent = new Intent(this, AdminReceiptsActivity.class);
                    }
                } else if (id == R.id.nav_batches) {
                    if (!(this instanceof AdminBatchesActivity)) {
                        intent = new Intent(this, AdminBatchesActivity.class);
                    }
                } else if (id == R.id.nav_transfers) {
                    if (!(this instanceof AdminTransfersActivity)) {
                        intent = new Intent(this, AdminTransfersActivity.class);
                    }
                } else if (id == R.id.nav_roles) {
                    if (!(this instanceof AdminRolesActivity)) {
                        intent = new Intent(this, AdminRolesActivity.class);
                    }
                } else if (id == R.id.nav_reviews) {
                    if (!(this instanceof AdminReviewsActivity)) {
                        intent = new Intent(this, AdminReviewsActivity.class);
                    }
                } else {
                    Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                
                if (intent != null) {
                    startActivity(intent);
                    if (!(this instanceof AdminDashboardActivity)) {
                        finish();
                    }
                }
                return true;
            });
        }
    }

    private void handleLogout() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
            .setPositiveButton("Đăng xuất", (dialog, which) -> {
                sessionManager.logout();
                
                // XÓA TOKEN KHỎI HỆ THỐNG MẠNG
                com.ptithcm.lottemart.data.remote.RetrofitClient.init(this);
                
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}
