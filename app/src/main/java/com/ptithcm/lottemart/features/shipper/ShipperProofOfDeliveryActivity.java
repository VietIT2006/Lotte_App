package com.ptithcm.lottemart.features.shipper;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.DeliveryApiService;
import com.ptithcm.lottemart.data.models.ShipperOrder;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperProofOfDeliveryActivity extends AppCompatActivity {

    private DeliveryApiService apiService;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_proof_of_delivery);

        orderId = getIntent().getStringExtra("ORDER_ID");
        apiService = RetrofitClient.getClient().create(DeliveryApiService.class);

        Button btnCapturePhoto = findViewById(R.id.btnCapturePhoto);
        Button btnSubmitDelivery = findViewById(R.id.btnSubmitDelivery);

        btnCapturePhoto.setOnClickListener(v -> {
            // Mở intent camera chụp hình
            Toast.makeText(this, "Mở Camera (Mockup)", Toast.LENGTH_SHORT).show();
        });

        btnSubmitDelivery.setOnClickListener(v -> {
            if (orderId == null || orderId.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy mã đơn hàng!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Gọi API cập nhật trạng thái đơn hàng thành hoàn thành (completed)
            btnSubmitDelivery.setEnabled(false);
            apiService.updateOrderStatus(orderId, new DeliveryApiService.UpdateStatusRequest("completed", "Giao hàng thành công"))
                .enqueue(new Callback<ApiResponse<ShipperOrder>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ShipperOrder>> call, Response<ApiResponse<ShipperOrder>> response) {
                        btnSubmitDelivery.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(ShipperProofOfDeliveryActivity.this, "Đã hoàn thành đơn hàng thành công!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(ShipperProofOfDeliveryActivity.this, "Lỗi cập nhật trạng thái: " + (response.body() != null ? response.body().getMessage() : "Server error"), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ShipperOrder>> call, Throwable t) {
                        btnSubmitDelivery.setEnabled(true);
                        Toast.makeText(ShipperProofOfDeliveryActivity.this, "Lỗi kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        });
    }
}
