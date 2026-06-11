package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.OrderApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Order;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView tvOrderDetailStatus, tvDetailOrderId, tvDetailOrderDate;
    private TextView tvDetailShippingFee, tvDetailPaymentMethod, tvDetailOrderTotal;
    private LinearLayout llOrderItemsContainer;
    
    // Stepper Indicators
    private View indicator1, indicator2, indicator3, indicator4;
    private TextView lblStep1, lblStep2, lblStep3, lblStep4;
    
    // Shipper Card & Buttons
    private CardView cvShipperInfo;
    private TextView tvShipperName, tvShipperVehicle;
    private ImageButton btnCallShipper;
    private MaterialButton btnTrackShipper, btnRateOrder;

    private String orderId;
    private SessionManager sessionManager;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        sessionManager = new SessionManager(this);
        orderId = getIntent().getStringExtra("ORDER_ID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        initViews();
        loadOrderDetail();
    }

    private void initViews() {
        tvOrderDetailStatus = findViewById(R.id.tvOrderDetailStatus);
        tvDetailOrderId = findViewById(R.id.tvDetailOrderId);
        tvDetailOrderDate = findViewById(R.id.tvDetailOrderDate);
        tvDetailShippingFee = findViewById(R.id.tvDetailShippingFee);
        tvDetailPaymentMethod = findViewById(R.id.tvDetailPaymentMethod);
        tvDetailOrderTotal = findViewById(R.id.tvDetailOrderTotal);
        llOrderItemsContainer = findViewById(R.id.llOrderItemsContainer);

        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);
        indicator3 = findViewById(R.id.indicator3);
        indicator4 = findViewById(R.id.indicator4);

        lblStep1 = findViewById(R.id.lblStep1);
        lblStep2 = findViewById(R.id.lblStep2);
        lblStep3 = findViewById(R.id.lblStep3);
        lblStep4 = findViewById(R.id.lblStep4);

        cvShipperInfo = findViewById(R.id.cvShipperInfo);
        tvShipperName = findViewById(R.id.tvShipperName);
        tvShipperVehicle = findViewById(R.id.tvShipperVehicle);
        btnCallShipper = findViewById(R.id.btnCallShipper);

        btnTrackShipper = findViewById(R.id.btnTrackShipper);
        btnRateOrder = findViewById(R.id.btnRateOrder);

        btnTrackShipper.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, OrderTrackingActivity.class);
            intent.putExtra("ORDER_ID", orderId);
            String address = "469 Nguyễn Hữu Thọ, Tân Hưng, Quận 7, TP HCM";
            if ("69c9daead9fb80416235e662".equals(sessionManager.getUserId())) {
                address = "12 Lê Duẩn, Bến Nghé, Quận 1, TP. HCM";
            }
            intent.putExtra("CUSTOMER_ADDRESS", address);
            startActivity(intent);
        });

        btnRateOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng đánh giá đang được phát triển!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadOrderDetail() {
        OrderApiService api = RetrofitClient.getClient().create(OrderApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();

        api.getAdminOrders(token).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // Lọc đơn hàng theo ID
                    for (Order order : response.body().getData()) {
                        if (order.getId().equals(orderId)) {
                            currentOrder = order;
                            displayOrder(order);
                            return;
                        }
                    }
                    Toast.makeText(OrderDetailActivity.this, "Không tìm thấy chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Lỗi tải chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrder(Order order) {
        tvDetailOrderId.setText("Mã đơn hàng: #" + order.getId());
        
        String rawDate = order.getCreatedAt();
        if (rawDate != null && rawDate.contains("T")) {
            rawDate = rawDate.replace("T", " ").substring(0, 16);
        }
        tvDetailOrderDate.setText("Ngày đặt: " + (rawDate != null ? rawDate : "Chưa xác định"));
        tvDetailShippingFee.setText(String.format("%,.0f đ", order.getShippingFee()));
        tvDetailPaymentMethod.setText(order.getPaymentMethod());
        tvDetailOrderTotal.setText(String.format("%,.0f đ", order.getTotalAmount() + order.getShippingFee()));

        // Populate items list
        llOrderItemsContainer.removeAllViews();
        if (order.getItems() != null) {
            for (Order.OrderItem item : order.getItems()) {
                View itemView = LayoutInflater.from(this).inflate(R.layout.user_item_address, null);
                // Dùng custom view hoặc text view đơn giản
                TextView tvNamePhone = itemView.findViewById(R.id.tvNamePhone);
                TextView tvAddressDetails = itemView.findViewById(R.id.tvAddressDetails);
                TextView tvLabel = itemView.findViewById(R.id.tvLabel);
                
                // Ẩn các nút không cần thiết
                itemView.findViewById(R.id.btnSelect).setVisibility(View.GONE);
                itemView.findViewById(R.id.btnDeleteAddress).setVisibility(View.GONE);
                itemView.findViewById(R.id.tvDefaultBadge).setVisibility(View.GONE);

                tvLabel.setText("MÓN");
                tvLabel.setTextColor(Color.parseColor("#E53935"));
                tvNamePhone.setText(item.getProductName());
                tvAddressDetails.setText("Số lượng: " + item.getQuantity() + " | Đơn giá: " + String.format("%,.0f đ", item.getPrice()));

                llOrderItemsContainer.addView(itemView);
            }
        }

        updateStepper(order.getStatus());
    }

    private void updateStepper(String status) {
        int activeColor = Color.parseColor("#E53935"); // Red
        int inactiveColor = Color.parseColor("#E0E0E0"); // Grey
        
        // Reset all to inactive
        indicator1.getBackground().setTint(inactiveColor);
        indicator2.getBackground().setTint(inactiveColor);
        indicator3.getBackground().setTint(inactiveColor);
        indicator4.getBackground().setTint(inactiveColor);
        
        lblStep1.setTextColor(Color.GRAY);
        lblStep2.setTextColor(Color.GRAY);
        lblStep3.setTextColor(Color.GRAY);
        lblStep4.setTextColor(Color.GRAY);

        if (status == null) status = "PENDING";
        
        tvOrderDetailStatus.setText("Trạng thái: " + getStatusText(status));

        switch (status.toUpperCase()) {
            case "PENDING":
                indicator1.getBackground().setTint(activeColor);
                lblStep1.setTextColor(activeColor);
                lblStep1.setTextSize(12);
                break;
            case "ACCEPTED":
            case "PREPARING":
                indicator1.getBackground().setTint(activeColor);
                indicator2.getBackground().setTint(activeColor);
                lblStep2.setTextColor(activeColor);
                lblStep2.setTextSize(12);
                break;
            case "SHIPPING":
            case "PICKED_UP":
                indicator1.getBackground().setTint(activeColor);
                indicator2.getBackground().setTint(activeColor);
                indicator3.getBackground().setTint(activeColor);
                lblStep3.setTextColor(activeColor);
                lblStep3.setTextSize(12);

                // Show Shipper card and Live Map button
                cvShipperInfo.setVisibility(View.VISIBLE);
                btnTrackShipper.setVisibility(View.VISIBLE);
                
                // Mock shipper info
                tvShipperName.setText("Tài xế Nguyễn Văn A");
                tvShipperVehicle.setText("Lotte Express • Xe máy (29X1-2345)");
                btnCallShipper.setOnClickListener(v -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0912345678"));
                    startActivity(callIntent);
                });
                break;
            case "DELIVERED":
                indicator1.getBackground().setTint(activeColor);
                indicator2.getBackground().setTint(activeColor);
                indicator3.getBackground().setTint(activeColor);
                indicator4.getBackground().setTint(activeColor);
                lblStep4.setTextColor(activeColor);
                lblStep4.setTextSize(12);

                cvShipperInfo.setVisibility(View.VISIBLE);
                btnRateOrder.setVisibility(View.VISIBLE);
                tvShipperName.setText("Tài xế Nguyễn Văn A");
                tvShipperVehicle.setText("Giao hàng thành công!");
                btnCallShipper.setVisibility(View.GONE);
                break;
            case "CANCELLED":
                tvOrderDetailStatus.setText("Trạng thái: Đã hủy");
                tvOrderDetailStatus.setTextColor(Color.RED);
                break;
        }
    }

    private String getStatusText(String status) {
        switch (status.toUpperCase()) {
            case "PENDING": return "Chờ xác nhận";
            case "ACCEPTED":
            case "PREPARING": return "Đang soạn hàng";
            case "SHIPPING":
            case "PICKED_UP": return "Đang giao hàng";
            case "DELIVERED": return "Đã nhận hàng";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }
}
