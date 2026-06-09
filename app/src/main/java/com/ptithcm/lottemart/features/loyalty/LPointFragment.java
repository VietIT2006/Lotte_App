package com.ptithcm.lottemart.features.loyalty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.PromotionApiService;
import com.ptithcm.lottemart.data.models.Coupon;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.features.shopping.CouponAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LPointFragment extends Fragment {
    private static final String TAG = "LPointFragment";
    private RecyclerView rvCoupons;
    private CouponAdapter adapter;
    private List<Coupon> couponList;
    private PromotionApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment_lpoint, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCoupons = view.findViewById(R.id.rvCoupons);
        rvCoupons.setLayoutManager(new LinearLayoutManager(getContext()));

        couponList = new ArrayList<>();
        // Trong tab Voucher chính, bấm "Dùng" chỉ cần hiện thông báo
        adapter = new CouponAdapter(getContext(), couponList, coupon -> {
            Toast.makeText(getContext(), "Voucher " + coupon.getCode() + " đã được lưu. Áp dụng khi thanh toán trong Giỏ hàng!", Toast.LENGTH_LONG).show();
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
                    Log.e(TAG, "Failed to load coupons: " + response.message());
                    loadMockCoupons();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Coupon>>> call, Throwable t) {
                Log.e(TAG, "Failure loading coupons", t);
                loadMockCoupons();
            }
        });
    }

    private void loadMockCoupons() {
        couponList.clear();
        couponList.add(new Coupon("WELCOME50", "Giảm 50.000đ cho thành viên mới", 50000, "", true));
        couponList.add(new Coupon("SALE20", "Giảm 20.000đ cho đơn hàng bách hóa", 20000, "", true));
        couponList.add(new Coupon("FREESHIP", "Freeship tối đa 25.000đ", 25000, "", true));
        couponList.add(new Coupon("GIAM10", "Giảm 10.000đ cho đơn hàng đồ tươi", 10000, "", true));
        adapter.notifyDataSetChanged();
    }
}
