package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.Order;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderApiService {
    @GET("/api/v1/ordering/admin/orders")
    Call<ApiResponse<List<Order>>> getAdminOrders(@Header("Authorization") String token);

    @PUT("/api/v1/ordering/admin/orders/{id}/status")
    Call<ApiResponse<Order>> updateOrderStatus(
            @Header("Authorization") String token,
            @Path("id") String orderId,
            @Body UpdateOrderStatusRequest request
    );

    class UpdateOrderStatusRequest {
        private String status;
        public UpdateOrderStatusRequest(String status) {
            this.status = status;
        }
    }
}
