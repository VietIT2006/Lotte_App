package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.Branch;
import com.ptithcm.lottemart.data.models.Category;
import com.ptithcm.lottemart.data.models.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Body;

public interface ProductApiService {
    @GET("catalog/categories")
    Call<ApiResponse<List<Category>>> getCategories();

    @GET("catalog/branches")
    Call<ApiResponse<List<Branch>>> getBranches();

    @GET("catalog/products")
    Call<ApiResponse<List<Product>>> getProducts(@Query("category_id") String categoryId);

    @GET("catalog/products/featured")
    Call<ApiResponse<List<Product>>> getFeaturedProducts();

    @GET("catalog/products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") String id);

    @GET("catalog/products/search")
    Call<ApiResponse<List<Product>>> searchProducts(@Query("q") String query, @Query("sort_by") String sortBy);

    @PUT("/api/v1/catalog/branches/{id}")
    Call<ApiResponse<Branch>> updateBranch(@Header("Authorization") String token, @Path("id") String id, @Body Branch branch);

    @DELETE("/api/v1/catalog/branches/{id}")
    Call<ApiResponse<Void>> deleteBranch(@Header("Authorization") String token, @Path("id") String id);

    // --- REVIEWS ---
    @GET("/api/v1/catalog/admin/reviews")
    Call<ApiResponse<List<com.ptithcm.lottemart.data.models.Review>>> getAdminReviews(@Header("Authorization") String token);

    @DELETE("/api/v1/catalog/admin/reviews/{id}")
    Call<ApiResponse<Void>> deleteReview(@Header("Authorization") String token, @Path("id") String id);
}
