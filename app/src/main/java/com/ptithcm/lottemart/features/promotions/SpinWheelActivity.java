package com.ptithcm.lottemart.features.promotions;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.ui.views.SpinWheelView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpinWheelActivity extends AppCompatActivity {

    private SpinWheelView spinWheelView;
    private Button btnSpin;
    private TextView tvRemainingSpins;
    private TextView tvDescription;
    private ProductApiService apiService;
    private SessionManager sessionManager;
    private boolean isSpinning = false;
    private float currentDegree = 0f;
    private ProductApiService.SpinEvent currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_spin_wheel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        spinWheelView = findViewById(R.id.spinWheelView);
        btnSpin = findViewById(R.id.btnSpin);
        tvRemainingSpins = findViewById(R.id.tvRemainingSpins);
        tvDescription = findViewById(R.id.tvDescription);

        apiService = RetrofitClient.getClient().create(ProductApiService.class);
        sessionManager = new SessionManager(this);

        btnSpin.setOnClickListener(v -> performSpin());

        loadActiveEvent();
    }

    private void loadActiveEvent() {
        apiService.getActiveSpinEvent().enqueue(new Callback<ApiResponse<ProductApiService.SpinEvent>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductApiService.SpinEvent>> call, Response<ApiResponse<ProductApiService.SpinEvent>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentEvent = response.body().getData();
                    if (currentEvent != null && currentEvent.getRewards() != null) {
                        spinWheelView.setRewards(currentEvent.getRewards());
                        tvDescription.setText(currentEvent.getName());
                    }
                } else {
                    Toast.makeText(SpinWheelActivity.this, "Không thể tải sự kiện vòng quay.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductApiService.SpinEvent>> call, Throwable t) {
                Toast.makeText(SpinWheelActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSpin() {
        if (isSpinning || currentEvent == null) return;
        
        String token = sessionManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để tham gia", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSpin.setEnabled(false);
        isSpinning = true;

        apiService.playSpin("Bearer " + token).enqueue(new Callback<ApiResponse<ProductApiService.SpinPlayResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductApiService.SpinPlayResponse>> call, Response<ApiResponse<ProductApiService.SpinPlayResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ProductApiService.SpinPlayResponse result = response.body().getData();
                    tvRemainingSpins.setText("Số lượt quay còn lại: " + result.getRemainingSpins());
                    animateSpin(result.getRewardIndex(), result.getReward());
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Lỗi khi quay!";
                    Toast.makeText(SpinWheelActivity.this, msg, Toast.LENGTH_LONG).show();
                    resetSpinState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductApiService.SpinPlayResponse>> call, Throwable t) {
                Toast.makeText(SpinWheelActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                resetSpinState();
            }
        });
    }

    private void animateSpin(int targetIndex, ProductApiService.SpinEvent.Reward reward) {
        int totalRewards = currentEvent.getRewards().size();
        float sliceAngle = 360f / totalRewards;
        
        // Target angle points to the middle of the selected slice
        // Note: The pointer is at the TOP (270 degrees in Android Canvas, or effectively 0 relative to standard if we adjust).
        // Let's calculate: the slice starts at i * sliceAngle. Midpoint is i * sliceAngle + sliceAngle / 2.
        // We need to rotate the view so that this midpoint ends up at the top.
        // If we rotate clockwise, we subtract the target angle from 360 to bring it to top.
        
        float targetSliceMidAngle = targetIndex * sliceAngle + (sliceAngle / 2f);
        float endDegree = 360f - targetSliceMidAngle - 90f; // -90 because pointer is at top
        
        // Add multiple full rotations for effect
        endDegree += 360f * 5; // 5 full spins

        final float finalEndDegree = endDegree;

        RotateAnimation rotateAnimation = new RotateAnimation(
                0, endDegree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setDuration(3000); // 3 seconds
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());

        rotateAnimation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {}

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                currentDegree = finalEndDegree % 360f;
                showSuccessDialog(reward);
                resetSpinState();
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {}
        });

        spinWheelView.startAnimation(rotateAnimation);
    }

    private void showSuccessDialog(ProductApiService.SpinEvent.Reward reward) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_success);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvDialogMessage);

        if (tvTitle != null) tvTitle.setText("Chúc Mừng!");
        if (tvMessage != null) tvMessage.setText("Bạn đã trúng " + reward.getRewardName() + "!");

        dialog.show();

        // Auto-dismiss after 2 seconds (per user request)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 2000);
    }

    private void resetSpinState() {
        isSpinning = false;
        btnSpin.setEnabled(true);
    }
}
