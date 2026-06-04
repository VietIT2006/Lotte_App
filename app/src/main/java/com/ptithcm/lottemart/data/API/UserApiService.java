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

    class RoleUpdateRequest {
        private String role;
        public RoleUpdateRequest(String role) { this.role = role; }
    }

    @PUT("/api/v1/users/admin/{id}/role")
    Call<ApiResponse<User>> updateUserRole(@Header("Authorization") String token, @Path("id") String id, @Body RoleUpdateRequest request);
}
