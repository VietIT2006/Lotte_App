package com.ptithcm.lottemart.features.shipper;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.lottemart.R;

public class ShipperProofOfDeliveryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_proof_of_delivery);

        Button btnCapturePhoto = findViewById(R.id.btnCapturePhoto);
        Button btnSubmitDelivery = findViewById(R.id.btnSubmitDelivery);

        btnCapturePhoto.setOnClickListener(v -> {
            // Mở intent camera chụp hình
            Toast.makeText(this, "Mở Camera (Mockup)", Toast.LENGTH_SHORT).show();
        });

        btnSubmitDelivery.setOnClickListener(v -> {
            Toast.makeText(this, "Đã hoàn thành đơn hàng!", Toast.LENGTH_LONG).show();
            // Quay về màn hình Dashboard hoặc xoá activity khỏi stack
            finish();
        });
    }
}
