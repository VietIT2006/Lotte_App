package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.OrderApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Address;
import com.ptithcm.lottemart.data.models.CartItem;
import com.ptithcm.lottemart.data.models.Coupon;
import com.ptithcm.lottemart.data.models.Order;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private static final int REQUEST_CODE_ADDRESS = 1001;
    private static final int REQUEST_CODE_COUPON = 1002;

    private RecyclerView rvOrderItems;
    private CheckoutProductAdapter adapter;
    private List<CartItem> cartItems;

    private TextView tvReceiverName, tvReceiverPhone, tvReceiverAddress;
    private TextView tvSubtotal, tvShippingFee, tvDiscount, tvTotalAmount;
    private RadioGroup rgPaymentMethods;
    private MaterialButton btnPlaceOrder;

    private Address selectedAddress;
    private Coupon selectedCoupon;

    private double subtotal = 0;
    private final double shippingFee = 25000;
    private double discount = 0;
    private double totalAmount = 0;

    private SessionManager sessionManager;
    private OrderApiService orderApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_checkout);

        sessionManager = new SessionManager(this);
        orderApiService = RetrofitClient.getClient().create(OrderApiService.class);

        // Get checkout items from Intent
        cartItems = (List<CartItem>) getIntent().getSerializableExtra("cart_items");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        initViews();
        setupToolbar();
        setupOrderItems();
        calculatePrices();
        setupListeners();
        
        // Load default address mockup or use passed address from CartActivity
        Address passedAddress = (Address) getIntent().getSerializableExtra("selected_address");
        if (passedAddress != null) {
            selectedAddress = passedAddress;
            updateAddressUI();
        } else {
            loadDefaultAddress();
        }
    }

    private void initViews() {
        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvReceiverPhone = findViewById(R.id.tvReceiverPhone);
        tvReceiverAddress = findViewById(R.id.tvReceiverAddress);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        rvOrderItems = findViewById(R.id.rvOrderItems);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void setupOrderItems() {
        adapter = new CheckoutProductAdapter(this, cartItems);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(adapter);
    }

    private void calculatePrices() {
        subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += (item.getProduct().getPrice() * item.getQuantity());
        }
        
        if (selectedCoupon != null) {
            discount = selectedCoupon.getDiscountValue();
        } else {
            discount = 0;
        }

        totalAmount = subtotal + shippingFee - discount;
        if (totalAmount < 0) totalAmount = 0;

        tvSubtotal.setText(String.format("%,.0fđ", subtotal));
        tvShippingFee.setText(String.format("%,.0fđ", shippingFee));
        tvDiscount.setText(String.format("-%,.0fđ", discount));
        tvTotalAmount.setText(String.format("%,.0fđ", totalAmount));
    }

    private void loadDefaultAddress() {
        com.ptithcm.lottemart.data.api.UserApiService userApiService = RetrofitClient.getClient().create(com.ptithcm.lottemart.data.api.UserApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();
        userApiService.getProfile(token).enqueue(new Callback<ApiResponse<com.ptithcm.lottemart.data.models.User>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.ptithcm.lottemart.data.models.User>> call, Response<ApiResponse<com.ptithcm.lottemart.data.models.User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    com.ptithcm.lottemart.data.models.User user = response.body().getData();
                    String name = user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername();
                    String phone = user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "";
                    String addressText = user.getAddress() != null && !user.getAddress().isEmpty() ? user.getAddress() : "";
                    
                    // Nếu địa chỉ trống, bắt buộc người dùng chọn địa chỉ mới
                    if (addressText.isEmpty()) {
                        selectedAddress = null;
                        tvReceiverName.setText("Chưa chọn người nhận");
                        tvReceiverPhone.setText("Vui lòng chọn địa chỉ nhận hàng");
                        tvReceiverAddress.setText("Chưa có địa chỉ giao hàng hợp lệ");
                    } else {
                        selectedAddress = new Address("user_addr", name, phone, addressText, "", "", "", true, "default");
                        updateAddressUI();
                    }
                } else {
                    selectedAddress = null;
                    tvReceiverName.setText("Chưa chọn người nhận");
                    tvReceiverPhone.setText("Vui lòng chọn địa chỉ nhận hàng");
                    tvReceiverAddress.setText("Chưa có địa chỉ giao hàng hợp lệ");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.ptithcm.lottemart.data.models.User>> call, Throwable t) {
                selectedAddress = null;
                tvReceiverName.setText("Chưa chọn người nhận");
                tvReceiverPhone.setText("Vui lòng chọn địa chỉ nhận hàng");
                tvReceiverAddress.setText("Chưa có địa chỉ giao hàng hợp lệ");
            }
        });
    }

    private void updateAddressUI() {
        if (selectedAddress != null) {
            tvReceiverName.setText("Người nhận: " + selectedAddress.getName());
            tvReceiverPhone.setText("Số điện thoại: " + selectedAddress.getPhone());
            tvReceiverAddress.setText("Địa chỉ: " + selectedAddress.getFullAddress());
        }
    }

    private void setupListeners() {
        findViewById(R.id.btnChangeAddress).setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, AddressBookActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADDRESS);
        });

        findViewById(R.id.btnSelectVoucher).setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, CouponWalletActivity.class);
            startActivityForResult(intent, REQUEST_CODE_COUPON);
        });

        btnPlaceOrder.setOnClickListener(v -> handlePlaceOrder());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_ADDRESS) {
                selectedAddress = (Address) data.getSerializableExtra("selected_address");
                updateAddressUI();
            } else if (requestCode == REQUEST_CODE_COUPON) {
                selectedCoupon = (Coupon) data.getSerializableExtra("selected_coupon");
                TextView btnSelectVoucher = findViewById(R.id.btnSelectVoucher);
                if (btnSelectVoucher != null && selectedCoupon != null) {
                    btnSelectVoucher.setText("Đang dùng: " + selectedCoupon.getCode());
                }
                calculatePrices();
            }
        }
    }

    private void handlePlaceOrder() {
        if (selectedAddress == null) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ nhận hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Đang xử lý...");

        // Chuẩn bị payload gửi cho backend
        List<OrderApiService.CheckoutItem> items = new ArrayList<>();
        for (CartItem item : cartItems) {
            items.add(new OrderApiService.CheckoutItem(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getProduct().getImageUrl(),
                    item.getQuantity(),
                    item.getProduct().getPrice()
            ));
        }

        String paymentMethod = "COD";
        int checkedId = rgPaymentMethods.getCheckedRadioButtonId();
        if (checkedId == R.id.rbMomo) {
            paymentMethod = "MoMo";
        } else if (checkedId == R.id.rbVNPAY) {
            paymentMethod = "VNPAY";
        }

        String token = "Bearer " + sessionManager.getAuthToken();
        OrderApiService.CheckoutRequest request = new OrderApiService.CheckoutRequest(
                items,
                totalAmount,
                shippingFee,
                paymentMethod
        );

        final String finalPaymentMethod = paymentMethod;

        orderApiService.checkout(token, request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                btnPlaceOrder.setEnabled(true);
                btnPlaceOrder.setText("ĐẶT HÀNG");

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Order createdOrder = response.body().getData();
                    processNextPaymentStep(createdOrder, finalPaymentMethod);
                } else {
                    // Dự phòng offline nếu Server chưa được cấu hình hoặc hết hạn
                    Log.e(TAG, "Server response failure: " + response.message());
                    simulateOfflineOrderSuccess(finalPaymentMethod);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                btnPlaceOrder.setEnabled(true);
                btnPlaceOrder.setText("ĐẶT HÀNG");
                Log.e(TAG, "Network request failure: ", t);
                simulateOfflineOrderSuccess(finalPaymentMethod);
            }
        });
    }

    private void processNextPaymentStep(Order order, String method) {
        if ("COD".equalsIgnoreCase(method)) {
            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CheckoutActivity.this, PaymentSuccessActivity.class);
            intent.putExtra("ORDER_ID", order.getId() != null ? order.getId() : "#LOTTE-MOCK");
            intent.putExtra("TOTAL_AMOUNT", String.format("%,.0fđ", totalAmount));
            intent.putExtra("CUSTOMER_ADDRESS", selectedAddress.getFullAddress());
            startActivity(intent);
            finish();
        } else {
            // Chuyển qua màn hình giả lập thanh toán online
            Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
            intent.putExtra("ORDER_ID", order.getId() != null ? order.getId() : "#LOTTE-MOCK");
            intent.putExtra("TOTAL_AMOUNT", totalAmount);
            intent.putExtra("PAYMENT_METHOD", method);
            intent.putExtra("CUSTOMER_ADDRESS", selectedAddress.getFullAddress());
            startActivity(intent);
            finish();
        }
    }

    private void simulateOfflineOrderSuccess(String method) {
        Toast.makeText(this, "Gặp sự cố mạng, đang sử dụng cổng thanh toán dự phòng!", Toast.LENGTH_SHORT).show();
        Order mockOrder = new Order();
        mockOrder.setStatus("PENDING");
        processNextPaymentStep(mockOrder, method);
    }
}
