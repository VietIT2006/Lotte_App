package com.ptithcm.lottemart.features.shipper;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ptithcm.lottemart.R;

public class ShipperDashboardActivity extends AppCompatActivity {

    private boolean isOnline = false;
    private Button btnToggleStatus;
    private Button btnViewOrders;
    private Button btnDeliveryHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_dashboard);

        btnToggleStatus = findViewById(R.id.btnToggleStatus);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnDeliveryHistory = findViewById(R.id.btnDeliveryHistory);

        btnToggleStatus.setOnClickListener(v -> {
            isOnline = !isOnline;
            if (isOnline) {
                btnToggleStatus.setText("ONLINE");
                btnToggleStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
                Toast.makeText(this, "Đã bật trạng thái nhận đơn", Toast.LENGTH_SHORT).show();
                // TODO: Call API to update status in shipper_profiles
            } else {
                btnToggleStatus.setText("OFFLINE");
                btnToggleStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9D9D9C")));
                Toast.makeText(this, "Đã tắt trạng thái nhận đơn", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewOrders.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, ShipperOrdersActivity.class);
            startActivity(intent);
        });

        btnDeliveryHistory.setOnClickListener(v -> {
            Toast.makeText(this, "Mở Lịch sử giao hàng", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to ShipperHistoryActivity
        });
    }
}
