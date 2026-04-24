package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.CartItem;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.ui.adapters.CartItemAdapter;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_cart);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup RecyclerView Mock Data
        RecyclerView rvCart = findViewById(R.id.rvCartItems);
        if (rvCart != null) {
            List<CartItem> mockItems = new ArrayList<>();
            mockItems.add(new CartItem("1", new Product("p1", "Sản phẩm A", 150000, 200000, ""), 2));
            mockItems.add(new CartItem("2", new Product("p2", "Sản phẩm B", 350000, 420000, ""), 1));
            
            CartItemAdapter adapter = new CartItemAdapter(this, mockItems);
            rvCart.setLayoutManager(new LinearLayoutManager(this));
            rvCart.setAdapter(adapter);
        }

        Button btnCheckout = findViewById(R.id.btnCheckout);
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                Intent intent = new Intent(CartActivity.this, OrderTrackingActivity.class);
                startActivity(intent);
            });
        }
    }
}
