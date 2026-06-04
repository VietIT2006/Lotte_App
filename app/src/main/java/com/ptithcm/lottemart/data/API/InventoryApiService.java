package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.InventoryBatch;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

import com.ptithcm.lottemart.data.models.StockMovement;

public interface InventoryApiService {
    @GET("/api/v1/inventory/admin/batches")
    Call<ApiResponse<List<InventoryBatch>>> getBatches(@Header("Authorization") String token);

    @GET("/api/v1/inventory/admin/movements")
    Call<ApiResponse<List<StockMovement>>> getAdminStockMovements(@Header("Authorization") String token);
}
