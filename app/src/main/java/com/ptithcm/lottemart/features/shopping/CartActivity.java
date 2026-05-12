package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.CartItem;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.ui.adapters.CartItemAdapter;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.ptithcm.lottemart.data.api.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private RecyclerView rvCart;
    private CartItemAdapter adapter;
    private ProductApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Setup RecyclerView
        rvCart = findViewById(R.id.rvCartItems);
        if (rvCart != null) {
            adapter = new CartItemAdapter(this, new ArrayList<>());
            rvCart.setLayoutManager(new LinearLayoutManager(this));
            rvCart.setAdapter(adapter);
            
            apiService = RetrofitClient.getClient().create(ProductApiService.class);
            fetchCartItems();
        }

        Button btnCheckout = findViewById(R.id.btnCheckout);
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                Intent intent = new Intent(CartActivity.this, OrderTrackingActivity.class);
                startActivity(intent);
            });
        }
    }

    private void fetchCartItems() {
        // SỬA LỖI: Xóa chữ 'r' thừa trong 'lottermart' thành 'lottemart'
        retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> call = apiService.getFeaturedProducts();
        call.enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData();
                    List<CartItem> cartItems = new ArrayList<>();
                    
                    if (products != null) {
                        for (int i = 0; i < products.size(); i++) {
                            Product p = products.get(i);
                            cartItems.add(new CartItem(String.valueOf(i + 1), p, 1));
                        }
                    }
                    
                    adapter.setItems(cartItems);
                } else {
                    Log.e(TAG, "Failed to fetch cart items: " + response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> call, Throwable t) {
                Log.e(TAG, "Error fetching cart items", t);
                Toast.makeText(CartActivity.this, "Không thể kết nối đến Supabase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
