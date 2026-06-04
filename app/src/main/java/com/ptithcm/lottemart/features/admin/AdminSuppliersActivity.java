package com.ptithcm.lottemart.features.admin;
import com.ptithcm.lottemart.data.models.Supplier;
import com.ptithcm.lottemart.data.api.PurchasingApiService;
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

public class AdminSuppliersActivity extends BaseAdminActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_suppliers);

        setHeaderTitle("Quản lý Nhà cung cấp");

        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
                AdminSupplierAdapter adapter = new AdminSupplierAdapter(this, new ArrayList<>());
        rvList.setAdapter(adapter);

        SessionManager sessionManager = new SessionManager(this);
        String token = "Bearer " + sessionManager.getAuthToken();
        RetrofitClient.getClient().create(PurchasingApiService.class).getSuppliers(token).enqueue(new Callback<ApiResponse<List<Supplier>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Supplier>>> call, Response<ApiResponse<List<Supplier>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setsuppliers(response.body().getData());
                } else if (response.code() == 403 || response.code() == 401) {
                    Toast.makeText(AdminSuppliersActivity.this, "Bạn không có quyền xem mục này", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AdminSuppliersActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Supplier>>> call, Throwable t) {
                Toast.makeText(AdminSuppliersActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });

        }
}
