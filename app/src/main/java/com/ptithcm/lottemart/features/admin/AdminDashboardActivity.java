package com.ptithcm.lottemart.features.admin;

import android.os.Bundle;
import com.ptithcm.lottemart.R;

public class AdminDashboardActivity extends BaseAdminActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        setHeaderTitle("Bảng điều khiển");

        android.content.SharedPreferences prefs = getSharedPreferences("CrashLogs", android.content.Context.MODE_PRIVATE);
        String lastCrash = prefs.getString("last_crash", null);
        if (lastCrash != null) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Crash Detected");
            builder.setMessage(lastCrash);
            builder.setPositiveButton("OK", null);
            builder.show();
            prefs.edit().remove("last_crash").apply();
        }
    }
}
