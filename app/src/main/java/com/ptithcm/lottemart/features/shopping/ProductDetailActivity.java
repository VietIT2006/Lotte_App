package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.ptithcm.lottemart.R;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_product_detail);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        if (btnAddToCart != null) {
            btnAddToCart.setOnClickListener(v -> {
                Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }
    }
}
