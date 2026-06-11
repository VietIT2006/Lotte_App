package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.ShipperOrder;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
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

    // Lấy thông tin ví shipper
    @GET("delivery/shipper/wallet")
    Call<ApiResponse<WalletInfo>> getWalletInfo();

    // Cập nhật trạng thái hoạt động của shipper (online/offline)
    @PATCH("delivery/shipper/status")
    Call<ApiResponse<Void>> updateShipperStatus(@Body UpdateStatusPayload payload);

    // Nạp tiền vào ví shipper
    @POST("delivery/shipper/wallet/topup")
    Call<ApiResponse<WalletInfo>> topupWallet(@Body TopupPayload payload);

    @POST("payments/wallet-link")
    Call<ApiResponse<PayosWalletLinkResponse>> createWalletPayosLink(@Body PayosWalletLinkRequest request);

    class PayosWalletLinkRequest {
        public double amount;
        public PayosWalletLinkRequest(double amount) {
            this.amount = amount;
        }
    }

    class PayosWalletLinkResponse {
        public String checkoutUrl;
        public String orderCode;
    }

    class TopupPayload {
        public double amount;
        public TopupPayload(double amount) {
            this.amount = amount;
        }
    }

    class UpdateStatusRequest {
        public String status;
        public String note;
        public Location location;

        public UpdateStatusRequest(String status, String note) {
            this.status = status;
            this.note = note;
        }
    }

    class UpdateStatusPayload {
        public String status;
        public UpdateStatusPayload(String status) {
            this.status = status;
        }
    }

    class WalletInfo {
        public double balance;
        public List<WalletTransaction> transactions;
    }

    class WalletTransaction {
        public String id;
        public double amount;
        public String type;
        public String description;
        public String created_at;
    }

    class Location {
        public double lat;
        public double lng;
    }
}
