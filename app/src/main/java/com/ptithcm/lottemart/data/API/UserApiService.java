package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Body;

public interface UserApiService {
    @GET("/api/v1/users")
    Call<ApiResponse<List<User>>> getUsers(@Header("Authorization") String token);

    @GET("/api/v1/users/profile")
    Call<ApiResponse<User>> getProfile(@Header("Authorization") String token);

    @PUT("/api/v1/users/profile")
    Call<ApiResponse<User>> updateProfile(@Header("Authorization") String token, @Body UserProfileUpdateRequest request);

    class RoleUpdateRequest {
        private String role;
        public RoleUpdateRequest(String role) { this.role = role; }
    }

    class UserProfileUpdateRequest {
        private String full_name;
        private String phone;
        private String address;

        public UserProfileUpdateRequest(String full_name, String phone, String address) {
            this.full_name = full_name;
            this.phone = phone;
            this.address = address;
        }
    }

    @PUT("/api/v1/users/admin/{id}/role")
    Call<ApiResponse<User>> updateUserRole(@Header("Authorization") String token, @Path("id") String id, @Body RoleUpdateRequest request);
}
