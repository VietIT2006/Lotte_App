package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.api.OrderApiService;
import com.ptithcm.lottemart.data.models.CartItem;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.ui.adapters.CartItemAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";
    private RecyclerView rvCart;
    private CartItemAdapter adapter;
    private ProductApiService apiService;
    private com.ptithcm.lottemart.data.local.SessionManager sessionManager;
    private com.ptithcm.lottemart.data.api.OrderApiService orderApiService;
    private TextView tvTotalAmount;
    private View emptyCartContainer, cartContent, bottomBar;
    private List<CartItem> currentCartItems = new ArrayList<>();

    private static final int REQUEST_CODE_ADDRESS = 1001;
    private TextView tvCartAddressTitle, tvCartAddressDetail;
    private com.ptithcm.lottemart.data.models.Address selectedAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCart = view.findViewById(R.id.rvCartItems);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        emptyCartContainer = view.findViewById(R.id.emptyCartContainer);
        cartContent = view.findViewById(R.id.cartContent);
        bottomBar = view.findViewById(R.id.bottomBar);
        tvCartAddressTitle = view.findViewById(R.id.tvCartAddressTitle);
        tvCartAddressDetail = view.findViewById(R.id.tvCartAddressDetail);
        
        setupRecyclerView();

        view.findViewById(R.id.btnBackToHome).setOnClickListener(v -> {
            if (getActivity() instanceof com.ptithcm.lottemart.MainActivity) {
                ((com.ptithcm.lottemart.MainActivity) getActivity()).navigateToHome();
            }
        });

        apiService = RetrofitClient.getClient().create(ProductApiService.class);
        sessionManager = new com.ptithcm.lottemart.data.local.SessionManager(getContext());
        orderApiService = RetrofitClient.getClient().create(com.ptithcm.lottemart.data.api.OrderApiService.class);
        
        // Click to choose address
        View cvAddress = view.findViewById(R.id.cvAddress);
        if (cvAddress != null) {
            cvAddress.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AddressBookActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADDRESS);
            });
        }

        loadProfileAddress();
        fetchCartItems();

        Button btnCheckout = view.findViewById(R.id.btnCheckout);
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                if (currentCartItems.isEmpty()) {
                    Toast.makeText(getContext(), "Giỏ hàng rỗng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                intent.putExtra("cart_items", (java.io.Serializable) currentCartItems);
                if (selectedAddress != null) {
                    intent.putExtra("selected_address", selectedAddress);
                }
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new CartItemAdapter(getContext(), currentCartItems);
        adapter.setOnCartItemChangeListener(new CartItemAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                updateCartItemQuantity(currentCartItems.get(position), newQuantity);
            }

            @Override
            public void onItemDeleted(int position) {
                deleteCartItem(currentCartItems.get(position), position);
            }
        });
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(adapter);
    }

    private void updateCartItemQuantity(CartItem item, int newQty) {
        if (sessionManager == null || orderApiService == null || sessionManager.getAuthToken() == null) {
            updateTotalAmount();
            return;
        }
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
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Cập nhật số lượng thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderApiService.CartResponse>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteCartItem(CartItem item, int position) {
        if (sessionManager == null || orderApiService == null || sessionManager.getAuthToken() == null) {
            currentCartItems.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, currentCartItems.size());
            updateTotalAmount();
            checkEmptyState();
            return;
        }
        String token = "Bearer " + sessionManager.getAuthToken();
        orderApiService.removeFromCart(token, item.getProduct().getId()).enqueue(new Callback<ApiResponse<OrderApiService.CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderApiService.CartResponse>> call, Response<ApiResponse<OrderApiService.CartResponse>> response) {
                if (response.isSuccessful()) {
                    currentCartItems.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, currentCartItems.size());
                    updateTotalAmount();
                    checkEmptyState();
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Xóa sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderApiService.CartResponse>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkEmptyState() {
        if (currentCartItems.isEmpty()) {
            emptyCartContainer.setVisibility(View.VISIBLE);
            cartContent.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            emptyCartContainer.setVisibility(View.GONE);
            cartContent.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }
    }

    private void updateTotalAmount() {
        double total = 0;
        for (CartItem item : currentCartItems) {
            total += (item.getProduct().getPrice() * item.getQuantity());
        }
        tvTotalAmount.setText(String.format("%,.0f đ", total));
    }

    private void fetchCartItems() {
        if (sessionManager == null || orderApiService == null || sessionManager.getAuthToken() == null) {
            currentCartItems.clear();
            adapter.notifyDataSetChanged();
            updateTotalAmount();
            checkEmptyState();
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
                    checkEmptyState();
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Không thể tải giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                    currentCartItems.clear();
                    adapter.notifyDataSetChanged();
                    updateTotalAmount();
                    checkEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderApiService.CartResponse>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
                currentCartItems.clear();
                adapter.notifyDataSetChanged();
                updateTotalAmount();
                checkEmptyState();
            }
        });
    }



    private void loadProfileAddress() {
        if (sessionManager == null || !sessionManager.isLoggedIn()) return;
        String currentUserId = sessionManager.getUserId();
        
        // Mock default address from profile/session matching user ID logic in AddressBookActivity
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == android.app.Activity.RESULT_OK && requestCode == REQUEST_CODE_ADDRESS && data != null) {
            selectedAddress = (com.ptithcm.lottemart.data.models.Address) data.getSerializableExtra("selected_address");
            updateAddressUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCartItems();
    }
}
