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

    @GET("/api/v1/catalog/admin/pending-products")
    Call<ApiResponse<List<Product>>> getPendingProducts(@Header("Authorization") String token);

    @GET("/api/v1/catalog/admin/pending-categories")
    Call<ApiResponse<List<Category>>> getPendingCategories(@Header("Authorization") String token);

    @PUT("/api/v1/catalog/admin/products/{id}/approve")
    Call<ApiResponse<Product>> approveProduct(@Header("Authorization") String token, @Path("id") String id);

    @PUT("/api/v1/catalog/admin/categories/{id}/approve")
    Call<ApiResponse<Category>> approveCategory(@Header("Authorization") String token, @Path("id") String id);

    // --- PROMOTIONS (BANNERS) ---
    @GET("/api/v1/promotions")
    Call<ApiResponse<List<Promotion>>> getPromotions();

    class Promotion {
        @com.google.gson.annotations.SerializedName("_id")
        private String id;
        private String title;
        private String subtitle;
        private String description;
        private String banner_image;
        private String image_url;
        private String category_id;
        private String product_id;
        private String link;
        private String position;

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
        public String getDescription() { return description; }
        public String getBannerImage() { return banner_image; }
        public String getImageUrl() { return image_url; }
        public String getCategoryId() { return category_id; }
        public String getProductId() { return product_id; }
        public String getLink() { return link; }
        public String getPosition() { return position; }
    }
}
