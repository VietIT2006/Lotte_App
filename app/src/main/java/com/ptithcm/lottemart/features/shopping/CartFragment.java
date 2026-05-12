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
    private TextView tvTotalAmount;
    private View emptyCartContainer, cartContent, bottomBar;
    private List<CartItem> currentCartItems = new ArrayList<>();

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
        
        setupRecyclerView();

        view.findViewById(R.id.btnBackToHome).setOnClickListener(v -> {
            ((com.ptithcm.lottemart.MainActivity)getActivity()).navigateToHome();
        });

        apiService = RetrofitClient.getClient().create(ProductApiService.class);
        fetchCartItems();

        Button btnCheckout = view.findViewById(R.id.btnCheckout);
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                if (currentCartItems.isEmpty()) {
                    Toast.makeText(getContext(), "Giỏ hàng rỗng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), PaymentSuccessActivity.class);
                intent.putExtra("ORDER_ID", String.valueOf((int)(Math.random() * 10000)));
                intent.putExtra("TOTAL_AMOUNT", tvTotalAmount.getText().toString());
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new CartItemAdapter(getContext(), currentCartItems);
        adapter.setOnCartItemChangeListener(new CartItemAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                updateTotalAmount();
            }

            @Override
            public void onItemDeleted(int position) {
                currentCartItems.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, currentCartItems.size());
                updateTotalAmount();
                checkEmptyState();
            }
        });
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(adapter);
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
        apiService.getFeaturedProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData();
                    currentCartItems.clear();
                    
                    if (products != null && !products.isEmpty()) {
                        for (int i = 0; i < products.size(); i++) {
                            Product p = products.get(i);
                            currentCartItems.add(new CartItem(String.valueOf(i + 1), p, 1));
                        }
                    }
                    
                    adapter.notifyDataSetChanged();
                    updateTotalAmount();
                    checkEmptyState();
                } else {
                    Log.e(TAG, "Failed to fetch cart items: " + response.message());
                    checkEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                Log.e(TAG, "Error fetching cart items", t);
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
                checkEmptyState();
            }
        });
    }
}
