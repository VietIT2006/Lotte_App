package com.ptithcm.lottemart.features.admin;

import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.PurchasingApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.ImportOrder;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReceiptsActivity extends BaseAdminActivity {
    private AdminImportOrderAdapter adapter;
    private SessionManager sessionManager;
    private List<ImportOrder> receiptsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_receipts);

        setHeaderTitle("Quản lý Phiếu nhập");

        sessionManager = new SessionManager(this);

        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AdminImportOrderAdapter(this, receiptsList, order -> {});
        rvList.setAdapter(adapter);

        loadReceipts();
    }

    private void loadReceipts() {
        String token = "Bearer " + sessionManager.getAuthToken();
        RetrofitClient.getClient().create(PurchasingApiService.class)
            .getImportOrders(token)
            .enqueue(new Callback<ApiResponse<List<ImportOrder>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ImportOrder>>> call, Response<ApiResponse<List<ImportOrder>>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        receiptsList.clear();
                        
                        // Lọc các đơn hàng có trạng thái "received"
                        for (ImportOrder order : response.body().getData()) {
                            if ("received".equalsIgnoreCase(order.getStatus())) {
                                receiptsList.add(order);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else if (response.code() == 403 || response.code() == 401) {
                        Toast.makeText(AdminReceiptsActivity.this, "Bạn không có quyền xem mục này", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AdminReceiptsActivity.this, "Lỗi tải dữ liệu phiếu nhập", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ImportOrder>>> call, Throwable t) {
                    Toast.makeText(AdminReceiptsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}
