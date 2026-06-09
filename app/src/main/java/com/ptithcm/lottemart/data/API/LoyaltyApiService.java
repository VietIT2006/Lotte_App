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
}
