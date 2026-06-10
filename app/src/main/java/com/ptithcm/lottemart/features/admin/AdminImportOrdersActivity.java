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

public class AdminImportOrdersActivity extends BaseAdminActivity {
    private AdminImportOrderAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_import_orders);

        setHeaderTitle("Quản lý Đơn nhập");

        sessionManager = new SessionManager(this);

        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AdminImportOrderAdapter(this, new ArrayList<>(), order -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(AdminImportOrdersActivity.this)
                .setTitle("Xác nhận nhập kho")
                .setMessage("Bạn có chắc chắn muốn xác nhận nhập kho cho phiếu " + order.getOrderCode() + "? Hệ thống sẽ tự động cập nhật số lượng sản phẩm vào kho hàng.")
                .setPositiveButton("Nhập kho", (dialog, which) -> receiveOrder(order))
                .setNegativeButton("Hủy", null)
                .show();
        });
        rvList.setAdapter(adapter);

        loadImportOrders();
    }

    private void loadImportOrders() {
        String token = "Bearer " + sessionManager.getAuthToken();
        RetrofitClient.getClient().create(PurchasingApiService.class).getImportOrders(token).enqueue(new Callback<ApiResponse<List<ImportOrder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ImportOrder>>> call, Response<ApiResponse<List<ImportOrder>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setorders(response.body().getData());
                } else if (response.code() == 403 || response.code() == 401) {
                    Toast.makeText(AdminImportOrdersActivity.this, "Bạn không có quyền xem mục này", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AdminImportOrdersActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<ImportOrder>>> call, Throwable t) {
                Toast.makeText(AdminImportOrdersActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void receiveOrder(ImportOrder order) {
        String token = "Bearer " + sessionManager.getAuthToken();
        RetrofitClient.getClient().create(PurchasingApiService.class)
            .receiveImportOrder(token, order.getId())
            .enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminImportOrdersActivity.this, "Đã nhập kho thành công!", Toast.LENGTH_SHORT).show();
                        loadImportOrders();
                    } else {
                        Toast.makeText(AdminImportOrdersActivity.this, "Không thể xác nhận nhập kho!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(AdminImportOrdersActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}
