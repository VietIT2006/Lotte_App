package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.CartItem;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.ui.adapters.CartItemAdapter;
import com.ptithcm.lottemart.data.api.OrderApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.ptithcm.lottemart.data.api.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.Nullable;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private RecyclerView rvCart;
    private CartItemAdapter adapter;
    private OrderApiService orderApiService;
    private SessionManager sessionManager;
    private TextView tvTotalAmount;
    private List<CartItem> currentCartItems = new ArrayList<>();

    private static final int REQUEST_CODE_ADDRESS = 1001;
    private TextView tvCartAddressTitle, tvCartAddressDetail;
    private com.ptithcm.lottemart.data.models.Address selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_cart);

        sessionManager = new SessionManager(this);
        orderApiService = RetrofitClient.getClient().create(OrderApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvCartAddressTitle = findViewById(R.id.tvCartAddressTitle);
        tvCartAddressDetail = findViewById(R.id.tvCartAddressDetail);

        // Click to choose address
        findViewById(R.id.cvAddress).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, AddressBookActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADDRESS);
        });

        // Setup RecyclerView
        rvCart = findViewById(R.id.rvCartItems);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new CartItemAdapter(this, currentCartItems);
        adapter.setOnCartItemChangeListener(new CartItemAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                // Đồng bộ số lượng mới lên database
                updateCartItemQuantity(currentCartItems.get(position), newQuantity);
            }

            @Override
            public void onItemDeleted(int position) {
                // Gọi API delete item từ database
                deleteCartItem(currentCartItems.get(position), position);
            }
        });
        rvCart.setAdapter(adapter);

        loadProfileAddress();
        fetchCartItems();

        Button btnCheckout = findViewById(R.id.btnCheckout);
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                if (currentCartItems.isEmpty()) {
                    Toast.makeText(this, "Giỏ hàng rỗng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("cart_items", (java.io.Serializable) currentCartItems);
                if (selectedAddress != null) {
                    intent.putExtra("selected_address", selectedAddress);
                }
                startActivity(intent);
            });
        }
    }

    private void loadProfileAddress() {
        if (!sessionManager.isLoggedIn()) return;
        String currentUserId = sessionManager.getUserId();
        
        // Mock default address from profile/session for matching current user ID logic in AddressBookActivity
        if ("69c9daead9fb80416235e662".equals(currentUserId)) {
            selectedAddress = new com.ptithcm.lottemart.data.models.Address("1", "Thành Phạm Công", "0846183771", "12 Lê Duẩn", "Bến Nghé", "Quận 1", "TP. HCM", true, "home");
        } else if ("000000000000000000000001".equals(currentUserId)) {
            selectedAddress = new com.ptithcm.lottemart.data.models.Address("1", "Admin Lotte", "0901234567", "469 Nguyễn Hữu Thọ", "Tân Hưng", "Quận 7", "TP. HCM", true, "home");
        } else {
            selectedAddress = new com.ptithcm.lottemart.data.models.Address("1", sessionManager.getUserName(), "0900000000", "Địa chỉ mẫu của bạn", "Phường Bến Thành", "Quận 1", "TP. HCM", true, "home");
        }
        updateAddressUI();
    }

    private void updateAddressUI() {
        if (selectedAddress != null && tvCartAddressTitle != null && tvCartAddressDetail != null) {
            tvCartAddressTitle.setText("Giao đến: " + selectedAddress.getName() + " | " + selectedAddress.getPhone());
            tvCartAddressDetail.setText(selectedAddress.getFullAddress());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADDRESS && data != null) {
            selectedAddress = (com.ptithcm.lottemart.data.models.Address) data.getSerializableExtra("selected_address");
            updateAddressUI();
        }
    }

    private void fetchCartItems() {
        if (sessionManager.getAuthToken() == null) {
            return;
        }

        String token = "Bearer " + sessionManager.getAuthToken();
        orderApiService.getCart(token).enqueue(new Callback<ApiResponse<OrderApiService.CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderApiService.CartResponse>> call, Response<ApiResponse<OrderApiService.CartResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<OrderApiService.CartItemResponse> items = response.body().getData().getItems();
                    currentCartItems.clear();
                    if (items != null && !items.isEmpty()) {
                        for (OrderApiService.CartItemResponse item : items) {
                            currentCartItems.add(item.toCartItem());
                        }
                    }
                    adapter.notifyDataSetChanged();
                    updateTotalAmount();
                } else {
                    Toast.makeText(CartActivity.this, "Không thể tải giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderApiService.CartResponse>> call, Throwable t) {
                Log.e(TAG, "Error fetching cart items", t);
                Toast.makeText(CartActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartItemQuantity(CartItem item, int newQty) {
        String token = "Bearer " + sessionManager.getAuthToken();
        OrderApiService.UpdateCartQtyRequest req = new OrderApiService.UpdateCartQtyRequest(
            item.getProduct().getId(),
            newQty
        );
        
        orderApiService.updateCartQty(token, req).enqueue(new Callback<ApiResponse<OrderApiService.CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderApiService.CartResponse>> call, Response<ApiResponse<OrderApiService.CartResponse>> response) {
                if (response.isSuccessful()) {
                    updateTotalAmount();
                } else {
                    Toast.makeText(CartActivity.this, "Cập nhật số lượng thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderApiService.CartResponse>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCartItem(CartItem item, int position) {
        String token = "Bearer " + sessionManager.getAuthToken();
        orderApiService.removeFromCart(token, item.getProduct().getId()).enqueue(new Callback<ApiResponse<OrderApiService.CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderApiService.CartResponse>> call, Response<ApiResponse<OrderApiService.CartResponse>> response) {
                if (response.isSuccessful()) {
                    currentCartItems.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, currentCartItems.size());
                    updateTotalAmount();
                    Toast.makeText(CartActivity.this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Xóa sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderApiService.CartResponse>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalAmount() {
        double total = 0;
        for (CartItem item : currentCartItems) {
            total += (item.getProduct().getPrice() * item.getQuantity());
        }
        if (tvTotalAmount != null) {
            tvTotalAmount.setText(String.format("%,.0f đ", total));
        }
    }
}
