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
        
        adapter = new CartItemAdapter(getContext(), new ArrayList<>());
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ProductApiService.class);
        fetchCartItems();

        Button btnCheckout = view.findViewById(R.id.btnCheckout);
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), OrderTrackingActivity.class);
                startActivity(intent);
            });
        }
    }

    private void fetchCartItems() {
        apiService.getFeaturedProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData();
                    List<CartItem> cartItems = new ArrayList<>();
                    double total = 0;
                    
                    if (products != null) {
                        for (int i = 0; i < products.size(); i++) {
                            Product p = products.get(i);
                            cartItems.add(new CartItem(String.valueOf(i + 1), p, 1));
                            total += p.getPrice();
                        }
                    }
                    
                    adapter.setItems(cartItems);
                    tvTotalAmount.setText(String.format("%,.0f đ", total));
                } else {
                    Log.e(TAG, "Failed to fetch cart items: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                Log.e(TAG, "Error fetching cart items", t);
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
