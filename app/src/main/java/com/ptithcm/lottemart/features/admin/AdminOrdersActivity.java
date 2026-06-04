package com.ptithcm.lottemart.features.admin;
import com.ptithcm.lottemart.data.models.Order;
import com.ptithcm.lottemart.data.api.OrderApiService;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.local.SessionManager;
import java.util.List;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;

public class AdminOrdersActivity extends BaseAdminActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        setHeaderTitle("Quản lý Đơn hàng");

        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
                AdminOrderAdapter adapter = new AdminOrderAdapter(this, new ArrayList<>(), item -> {});
        rvList.setAdapter(adapter);

        SessionManager sessionManager = new SessionManager(this);
        String token = "Bearer " + sessionManager.getAuthToken();
        RetrofitClient.getClient().create(OrderApiService.class).getAdminOrders(token).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setorders(response.body().getData());
                } else if (response.code() == 403 || response.code() == 401) {
                    Toast.makeText(AdminOrdersActivity.this, "Bạn không có quyền xem mục này", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AdminOrdersActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Toast.makeText(AdminOrdersActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });

        }
}
