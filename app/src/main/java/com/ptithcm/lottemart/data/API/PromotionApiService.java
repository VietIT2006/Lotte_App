package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.Promotion;
import com.ptithcm.lottemart.data.models.Coupon;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PromotionApiService {
    @GET("/api/v1/promotions/admin/promotions")
    Call<ApiResponse<List<Promotion>>> getAdminPromotions(@Header("Authorization") String token);

    @POST("/api/v1/promotions/admin/promotions")
    Call<ApiResponse<Promotion>> createPromotion(@Header("Authorization") String token, @Body Promotion promotion);

    @PUT("/api/v1/promotions/admin/promotions/{id}")
    Call<ApiResponse<Promotion>> updatePromotion(@Header("Authorization") String token, @Path("id") String id, @Body Promotion promotion);

    @DELETE("/api/v1/promotions/admin/promotions/{id}")
    Call<ApiResponse<Void>> deletePromotion(@Header("Authorization") String token, @Path("id") String id);

    // --- ADMIN COUPONS ---
    @GET("/api/v1/promotions/admin/coupons")
    Call<ApiResponse<List<Coupon>>> getAdminCoupons(@Header("Authorization") String token);

    @POST("/api/v1/promotions/admin/coupons")
    Call<ApiResponse<Coupon>> createCoupon(@Header("Authorization") String token, @Body Coupon coupon);

    @PUT("/api/v1/promotions/admin/coupons/{id}")
    Call<ApiResponse<Coupon>> updateCoupon(@Header("Authorization") String token, @Path("id") String id, @Body Coupon coupon);

    @DELETE("/api/v1/promotions/admin/coupons/{id}")
    Call<ApiResponse<Void>> deleteCoupon(@Header("Authorization") String token, @Path("id") String id);
}
