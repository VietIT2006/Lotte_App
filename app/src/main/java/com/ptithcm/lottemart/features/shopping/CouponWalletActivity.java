package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.PromotionApiService;
import com.ptithcm.lottemart.data.models.Coupon;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponWalletActivity extends AppCompatActivity {
    private static final String TAG = "CouponWalletActivity";
    private RecyclerView rvCoupons;
    private CouponAdapter adapter;
    private List<Coupon> couponList;
    private PromotionApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_coupon_wallet);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rvCoupons = findViewById(R.id.rvCoupons);
        rvCoupons.setLayoutManager(new LinearLayoutManager(this));

        couponList = new ArrayList<>();
        adapter = new CouponAdapter(this, couponList, coupon -> {
            Intent intent = new Intent();
            intent.putExtra("selected_coupon", coupon);
            setResult(RESULT_OK, intent);
            finish();
        });
        rvCoupons.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(PromotionApiService.class);
        fetchCoupons();
    }

    private void fetchCoupons() {
        apiService.getCoupons().enqueue(new Callback<ApiResponse<List<Coupon>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Coupon>>> call, Response<ApiResponse<List<Coupon>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    couponList.clear();
                    couponList.addAll(response.body().getData());
                    if (couponList.isEmpty()) {
                        loadMockCoupons();
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e(TAG, "Failed to load coupons from API: " + response.message());
                    loadMockCoupons();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Coupon>>> call, Throwable t) {
                Log.e(TAG, "Network failure getting coupons, loading mock ones", t);
                loadMockCoupons();
            }
        });
    }

    private void loadMockCoupons() {
        couponList.clear();
        couponList.add(new Coupon("LOTTEMART10", "Giảm 10.000đ cho đơn hàng bất kỳ", 10000, "", true));
        couponList.add(new Coupon("FREESHIP", "Miễn phí vận chuyển tối đa 25.000đ", 25000, "", true));
        couponList.add(new Coupon("LOTTEMART50", "Giảm 50.000đ cho đơn hàng giá trị cao", 50000, "", true));
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Không tải được voucher online, đã tải ví voucher offline!", Toast.LENGTH_SHORT).show();
    }
}
