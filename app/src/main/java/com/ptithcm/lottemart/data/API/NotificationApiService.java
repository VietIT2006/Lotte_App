package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.models.Notification;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface NotificationApiService {
    @GET("notifications")
    Call<ApiResponse<List<Notification>>> getNotifications();

    @PATCH("notifications/{id}/read")
    Call<ApiResponse<Void>> markAsRead(@Path("id") String id);
}
