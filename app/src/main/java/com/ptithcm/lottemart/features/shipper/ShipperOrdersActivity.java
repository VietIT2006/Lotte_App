package com.ptithcm.lottemart.features.shipper;

import android.os.Bundle;
import android.widget.Toast;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_orders);

        rvShipperOrders = findViewById(R.id.rvShipperOrders);
        rvShipperOrders.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new ShipperOrderAdapter();
        adapter.setListener(order -> {
            Toast.makeText(this, "Mở chi tiết đơn: " + order.getId(), Toast.LENGTH_SHORT).show();
            android.content.Intent intent = new android.content.Intent(ShipperOrdersActivity.this, ShipperDeliveryDetailsActivity.class);
            intent.putExtra("ORDER_ID", order.getId().toString());
            startActivity(intent);
        });
        rvShipperOrders.setAdapter(adapter);

        // Giả sử RetrofitClient có phương thức getClient()
        // apiService = RetrofitClient.getClient().create(DeliveryApiService.class);
        
        loadOrders();
    }

    private void loadOrders() {
        // Dummy call - In real app, uncomment below code
        /*
        apiService.getShipperOrders("assigned").enqueue(new Callback<ApiResponse<List<ShipperOrder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShipperOrder>>> call, Response<ApiResponse<List<ShipperOrder>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setOrders(response.body().getData());
                } else {
                    Toast.makeText(ShipperOrdersActivity.this, "Lỗi tải đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShipperOrder>>> call, Throwable t) {
                Toast.makeText(ShipperOrdersActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
        */
        Toast.makeText(this, "Đang tải danh sách đơn hàng...", Toast.LENGTH_SHORT).show();
    }
}
