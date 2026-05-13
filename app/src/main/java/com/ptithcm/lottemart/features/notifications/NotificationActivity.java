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
                    updateUI();
                } else {
                    Toast.makeText(NotificationActivity.this, "Lỗi tải thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Notification>>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsRead(Notification notification) {
        apiService.markAsRead(notification.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    notification.setRead(true);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    private void markAllAsRead() {
        // Mock logic for "Mark all as read"
        for (Notification n : notificationList) {
            if (!n.isRead()) markAsRead(n);
        }
        Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
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
