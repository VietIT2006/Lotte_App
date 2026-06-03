package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.Branch;
import com.ptithcm.lottemart.data.models.Category;
import com.ptithcm.lottemart.data.models.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
}
