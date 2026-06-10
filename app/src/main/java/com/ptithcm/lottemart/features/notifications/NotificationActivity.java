package com.ptithcm.lottemart.features.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.NotificationApiService;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.models.Notification;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private LinearLayout layoutEmpty;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();
    private NotificationApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_notifications);

        initViews();
        setupRecyclerView();
        setupListeners();
        fetchNotifications();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        
        // Khởi tạo RetrofitClient với context hiện tại
        RetrofitClient.init(this);
        apiService = RetrofitClient.getClient().create(NotificationApiService.class);
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(this, notificationList, notification -> {
            // Xử lý khi click vào thông báo (ví dụ: đánh dấu đã đọc)
            if (!notification.isRead()) {
                markAsRead(notification);
            }
        });
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void setupListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvMarkAllRead).setOnClickListener(v -> markAllAsRead());
    }

    private void fetchNotifications() {
        apiService.getNotifications().enqueue(new Callback<ApiResponse<List<Notification>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Notification>>> call, Response<ApiResponse<List<Notification>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    notificationList.clear();
                    notificationList.addAll(response.body().getData());
                    if (notificationList.isEmpty()) {
                        addMockNotifications();
                    }
                    updateUI();
                } else {
                    addMockNotifications();
                    updateUI();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Notification>>> call, Throwable t) {
                addMockNotifications();
                updateUI();
            }
        });
    }

    private void addMockNotifications() {
        notificationList.clear();
        notificationList.add(createMockNotification("1", "Khuyến mãi chào hè - Giảm đến 50%", "Đại tiệc siêu thương hiệu Lotte Mart giảm sâu 50% các mặt hàng tươi sống và đồ gia dụng từ ngày 10/06 đến 20/06.", "PROMO", false, "2026-06-10T09:00:00.000Z"));
        notificationList.add(createMockNotification("2", "Đơn hàng đang giao đến bạn", "Đơn hàng Lotte Mart số #LM-982761 của bạn đã được đóng gói và đang được tài xế vận chuyển giao đến địa chỉ của bạn.", "ORDER", false, "2026-06-10T08:30:00.000Z"));
        notificationList.add(createMockNotification("3", "Tích lũy L-Point thành công", "Chúc mừng! Bạn đã nhận được +1,200 L-Point từ hóa đơn mua sắm siêu thị ngày hôm qua. Hãy tiếp tục tích lũy để đổi quà nhé.", "SYSTEM", true, "2026-06-09T17:15:00.000Z"));
        notificationList.add(createMockNotification("4", "Chào mừng thành viên mới!", "Chào mừng bạn đến với ứng dụng đi chợ online của Lotte Mart. Nhập mã LOTTENEW để được giảm ngay 50.000đ cho đơn hàng đầu tiên.", "SYSTEM", true, "2026-06-09T08:00:00.000Z"));
    }

    private Notification createMockNotification(String id, String title, String message, String type, boolean isRead, String createdAt) {
        Notification n = new Notification();
        n.setId(id);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setRead(isRead);
        n.setCreatedAt(createdAt);
        return n;
    }

    private void markAsRead(Notification notification) {
        notification.setRead(true);
        adapter.notifyDataSetChanged();
        markAsReadOnServer(notification.getId());
    }

    private void markAsReadOnServer(String id) {
        apiService.markAsRead(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {}

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    private void markAllAsRead() {
        boolean hasUnread = false;
        for (Notification n : notificationList) {
            if (!n.isRead()) {
                n.setRead(true);
                markAsReadOnServer(n.getId());
                hasUnread = true;
            }
        }
        if (hasUnread) {
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        if (notificationList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}
