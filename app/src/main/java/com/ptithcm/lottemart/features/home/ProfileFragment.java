package com.ptithcm.lottemart.features.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.features.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvLPointBalance;
    private android.widget.ImageView ivProfileAvatar;
    private androidx.cardview.widget.CardView cvLPoint;
    private android.widget.Button btnLogout;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sessionManager = new SessionManager(requireContext());
        
        initViews(view);
        displayUserInfo();
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserInfo();
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvLPointBalance = view.findViewById(R.id.tvLPointBalance);
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        cvLPoint = view.findViewById(R.id.cvLPoint);
        btnLogout = view.findViewById(R.id.btnLogout);
        
        android.widget.ImageButton btnEditProfile = view.findViewById(R.id.btnEditProfile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    private void displayUserInfo() {
        tvUserName.setText(sessionManager.getUserName());
        tvUserEmail.setText(sessionManager.getUserEmail());
        if (tvLPointBalance != null) {
            tvLPointBalance.setText(String.format("%,d điểm", sessionManager.getLottePoints()));
        }

        // Tải thông tin Profile thực tế từ Backend để đồng bộ điểm
        com.ptithcm.lottemart.data.api.UserApiService userApiService = 
            com.ptithcm.lottemart.data.remote.RetrofitClient.getClient().create(com.ptithcm.lottemart.data.api.UserApiService.class);
        
        String token = "Bearer " + sessionManager.getAuthToken();
        userApiService.getProfile(token).enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<com.ptithcm.lottemart.data.models.User>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<com.ptithcm.lottemart.data.models.User>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<com.ptithcm.lottemart.data.models.User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    com.ptithcm.lottemart.data.models.User user = response.body().getData();
                    sessionManager.saveLottePoints(user.getLottePoints());
                    if (tvLPointBalance != null) {
                        tvLPointBalance.setText(String.format("%,d điểm", user.getLottePoints()));
                    }
                    if (ivProfileAvatar != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        String avatarUrl = user.getAvatar();
                        if (avatarUrl.startsWith("/uploads")) {
                            avatarUrl = com.ptithcm.lottemart.data.remote.NetworkConfig.getBaseDomain() + avatarUrl;
                        }
                        ivProfileAvatar.setImageTintList(null);
                        com.bumptech.glide.Glide.with(ProfileFragment.this).load(avatarUrl).circleCrop().into(ivProfileAvatar);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<com.ptithcm.lottemart.data.models.User>> call, Throwable t) {
                // Lỗi mạng, sử dụng điểm offline đã có
            }
        });
    }

    private void setupListeners() {
        if (cvLPoint != null) {
            cvLPoint.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.loyalty.LPointActivity.class);
                startActivity(intent);
            });
        }

        View view = getView();
        if (view != null) {
            TextView tvMenuOrders = view.findViewById(R.id.tvMenuOrders);
            if (tvMenuOrders != null) {
                tvMenuOrders.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.shopping.OrderTrackingActivity.class);
                    intent.putExtra("ORDER_ID", "default_order_id");
                    String address = "469 Nguyễn Hữu Thọ, Tân Hưng, Quận 7, TP HCM"; // Mặc định cho admin
                    if ("69c9daead9fb80416235e662".equals(sessionManager.getUserId())) {
                        address = "12 Lê Duẩn, Bến Nghé, Quận 1, TP. HCM";
                    }
                    intent.putExtra("CUSTOMER_ADDRESS", address);
                    startActivity(intent);
                });
            }

            TextView tvMenuAddresses = view.findViewById(R.id.tvMenuAddresses);
            if (tvMenuAddresses != null) {
                tvMenuAddresses.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.shopping.AddressBookActivity.class);
                    startActivity(intent);
                });
            }

            TextView tvMenuNotifications = view.findViewById(R.id.tvMenuNotifications);
            if (tvMenuNotifications != null) {
                tvMenuNotifications.setOnClickListener(v -> {
                    android.widget.Toast.makeText(getActivity(), "Tính năng cài đặt thông báo đang được xây dựng!", android.widget.Toast.LENGTH_SHORT).show();
                });
            }

            TextView tvMenuHelp = view.findViewById(R.id.tvMenuHelp);
            if (tvMenuHelp != null) {
                tvMenuHelp.setOnClickListener(v -> {
                    android.widget.Toast.makeText(getActivity(), "Tính năng Trợ giúp & Phản hồi đang được xây dựng!", android.widget.Toast.LENGTH_SHORT).show();
                });
            }
        }

        btnLogout.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi Lotte Mart không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    sessionManager.logout();
                    
                    // XÓA TOKEN KHỎI HỆ THỐNG MẠNG
                    com.ptithcm.lottemart.data.remote.RetrofitClient.init(requireContext());
                    
                    // Quay lại màn hình đăng nhập
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
        });
    }
}
