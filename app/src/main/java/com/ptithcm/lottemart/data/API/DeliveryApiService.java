package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.ShipperOrder;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeliveryApiService {
    
    // Lấy danh sách đơn hàng được gán cho Shipper
    @GET("delivery/shipper/orders")
    Call<ApiResponse<List<ShipperOrder>>> getShipperOrders(@Query("status") String status);

    // Cập nhật trạng thái đơn hàng (đã lấy, đang giao, thành công)
    @PATCH("delivery/shipper/orders/{id}/status")
    Call<ApiResponse<ShipperOrder>> updateOrderStatus(
            @Path("id") String orderId,
            @Body UpdateStatusRequest request
    );

    class UpdateStatusRequest {
        public String status;
        public String note;
        public Location location;

        public UpdateStatusRequest(String status, String note) {
            this.status = status;
            this.note = note;
        }
    }

    class Location {
        public double lat;
        public double lng;
    }
}
