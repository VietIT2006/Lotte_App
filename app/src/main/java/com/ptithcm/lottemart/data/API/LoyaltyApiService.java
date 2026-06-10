package com.ptithcm.lottemart.data.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoyaltyApiService {
    @GET("/api/v1/loyalty/balance")
    Call<ApiResponse<PointsResponse>> getPointsBalance(@Header("Authorization") String token);

    @POST("/api/v1/loyalty/redeem")
    Call<ApiResponse<RedeemResponse>> redeemPoints(@Header("Authorization") String token, @Body RedeemRequest request);

    @POST("/api/v1/loyalty/transfer")
    Call<ApiResponse<TransferResponse>> transferPoints(@Header("Authorization") String token, @Body TransferRequest request);

    @POST("/api/v1/loyalty/topup")
    Call<ApiResponse<TopupResponse>> topupPoints(@Header("Authorization") String token, @Body TopupRequest request);

    @GET("/api/v1/loyalty/history")
    Call<ApiResponse<java.util.List<Transaction>>> getTransactionHistory(@Header("Authorization") String token);

    class PointsResponse {
        private int points;
        public int getPoints() { return points; }
    }

    class RedeemRequest {
        private int points;
        private String voucher_code;
        private String voucher_title;

        public RedeemRequest(int points, String voucher_code, String voucher_title) {
            this.points = points;
            this.voucher_code = voucher_code;
            this.voucher_title = voucher_title;
        }
    }

    class RedeemResponse {
        private int new_points;
        public int getNewPoints() { return new_points; }
    }

    class TransferRequest {
        private String recipient;
        private int amount;

        public TransferRequest(String recipient, int amount) {
            this.recipient = recipient;
            this.amount = amount;
        }
    }

    class TransferResponse {
        private int new_points;
        public int getNewPoints() { return new_points; }
    }

    class TopupRequest {
        private int amount;
        public TopupRequest(int amount) { this.amount = amount; }
    }

    class TopupResponse {
        private int new_points;
        public int getNewPoints() { return new_points; }
    }

    class Transaction {
        private String id;
        private int amount;
        private String type;
        private String reason;
        private String created_at;

        public String getId() { return id; }
        public int getAmount() { return amount; }
        public String getType() { return type; }
        public String getReason() { return reason; }
        public String getCreatedAt() { return created_at; }
    }
}
