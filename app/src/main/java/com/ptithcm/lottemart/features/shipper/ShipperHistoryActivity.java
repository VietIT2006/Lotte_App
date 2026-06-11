package com.ptithcm.lottemart.features.shipper;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.DeliveryApiService;
import com.ptithcm.lottemart.data.models.ShipperOrder;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperHistoryActivity extends AppCompatActivity {

    private RecyclerView rvShipperHistory;
    private ShipperOrderAdapter adapter;
    private DeliveryApiService apiService;
    private TextView tvEmptyHistory;
    private TextView tvHistoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_history);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        tvHistoryTitle = findViewById(R.id.tvHistoryTitle);
        rvShipperHistory = findViewById(R.id.rvShipperHistory);
        rvShipperHistory.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ShipperOrderAdapter();
        adapter.setHistoryMode(true);
        rvShipperHistory.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(DeliveryApiService.class);
        loadHistory();
    }

    private void loadHistory() {
        Toast.makeText(this, "Đang tải lịch sử giao hàng...", Toast.LENGTH_SHORT).show();
        apiService.getShipperOrders(null).enqueue(new Callback<ApiResponse<List<ShipperOrder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShipperOrder>>> call, Response<ApiResponse<List<ShipperOrder>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ShipperOrder> list = response.body().getData();
                    List<ShipperOrder> historyList = new ArrayList<>();
                    if (list != null) {
                        for (ShipperOrder order : list) {
                            String status = order.getStatus();
                            if ("completed".equalsIgnoreCase(status) || "delivered".equalsIgnoreCase(status) || "delivery_failed".equalsIgnoreCase(status)) {
                                historyList.add(order);
                            }
                        }
                    }

                    if (!historyList.isEmpty()) {
                        adapter.setOrders(historyList);
                        tvEmptyHistory.setVisibility(View.GONE);
                        rvShipperHistory.setVisibility(View.VISIBLE);
                        tvHistoryTitle.setText("Đơn hàng đã giao (" + historyList.size() + ")");
                    } else {
                        tvEmptyHistory.setVisibility(View.VISIBLE);
                        rvShipperHistory.setVisibility(View.GONE);
                        tvHistoryTitle.setText("Đơn hàng đã giao (0)");
                    }
                } else {
                    Toast.makeText(ShipperHistoryActivity.this, "Lỗi tải lịch sử từ máy chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShipperOrder>>> call, Throwable t) {
                Toast.makeText(ShipperHistoryActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
