package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("auth/login")
    Call<ApiResponse<AuthResponseData>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("auth/social-login")
    Call<ApiResponse<AuthResponseData>> socialLogin(@Body SocialLoginRequest request);
}
