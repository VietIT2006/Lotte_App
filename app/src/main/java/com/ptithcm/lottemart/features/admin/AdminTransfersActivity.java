package com.ptithcm.lottemart.features.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.InventoryApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.StockMovement;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTransfersActivity extends BaseAdminActivity {
    private RecyclerView rvList;
    private AdminStockMovementAdapter adapter;
    private SessionManager sessionManager;
    private InventoryApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_transfers);

        setHeaderTitle("Lịch sử Biến động kho");

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient().create(InventoryApiService.class);

        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminStockMovementAdapter(this, new ArrayList<>());
        rvList.setAdapter(adapter);

        fetchStockMovements();
    }

    private void fetchStockMovements() {
        String token = "Bearer " + sessionManager.getAuthToken();
        apiService.getAdminStockMovements(token).enqueue(new Callback<ApiResponse<List<StockMovement>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<StockMovement>>> call, Response<ApiResponse<List<StockMovement>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setMovements(response.body().getData());
                } else {
                    Toast.makeText(AdminTransfersActivity.this, "Lỗi lấy danh sách Biến động kho", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StockMovement>>> call, Throwable t) {
                Toast.makeText(AdminTransfersActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
