package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserApiService {
    @GET("/api/v1/users")
    Call<ApiResponse<List<User>>> getUsers(@Header("Authorization") String token);
}
