package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.OrderApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Order;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView rvOrders;
    private UserOrderAdapter adapter;
    private List<Order> orderList;
    private LinearLayout llEmptyState;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rvOrders = findViewById(R.id.rvOrders);
        llEmptyState = findViewById(R.id.llEmptyState);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        
        adapter = new UserOrderAdapter(this, orderList, order -> {
            Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID", order.getId());
            startActivity(intent);
        });
        rvOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        OrderApiService api = RetrofitClient.getClient().create(OrderApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();

        api.getMyOrders(token).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    orderList.clear();
                    orderList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    
                    if (orderList.isEmpty()) {
                        llEmptyState.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    } else {
                        llEmptyState.setVisibility(View.GONE);
                        rvOrders.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(OrderListActivity.this, "Không thể tải danh sách đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
