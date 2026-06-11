package com.ptithcm.lottemart.features.shipper;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.DeliveryApiService;
import com.ptithcm.lottemart.data.models.ShipperOrder;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperOrdersActivity extends AppCompatActivity {

    private RecyclerView rvShipperOrders;
    private ShipperOrderAdapter adapter;
    private DeliveryApiService apiService;

    private TextView tvOrdersTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_orders);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvOrdersTitle = findViewById(R.id.tvOrdersTitle);
        rvShipperOrders = findViewById(R.id.rvShipperOrders);
        rvShipperOrders.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new ShipperOrderAdapter();
        adapter.setListener(order -> {
            android.content.Intent intent = new android.content.Intent(ShipperOrdersActivity.this, ShipperDeliveryDetailsActivity.class);
            intent.putExtra("ORDER_ID", order.getId());
            intent.putExtra("SHIPPER_ORDER", order);
            startActivity(intent);
        });
        rvShipperOrders.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(DeliveryApiService.class);
        
        loadOrders();
    }

    private void loadOrders() {
        Toast.makeText(this, "Đang tải danh sách đơn hàng...", Toast.LENGTH_SHORT).show();
        apiService.getShipperOrders("assigned").enqueue(new Callback<ApiResponse<List<ShipperOrder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShipperOrder>>> call, Response<ApiResponse<List<ShipperOrder>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ShipperOrder> list = response.body().getData();
                    if (list != null) {
                        adapter.setOrders(list);
                        tvOrdersTitle.setText(list.size() + " Đơn hàng đang chờ");
                    } else {
                        tvOrdersTitle.setText("0 Đơn hàng đang chờ");
                    }
                } else {
                    Toast.makeText(ShipperOrdersActivity.this, "Lỗi tải đơn hàng từ hệ thống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShipperOrder>>> call, Throwable t) {
                Toast.makeText(ShipperOrdersActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
