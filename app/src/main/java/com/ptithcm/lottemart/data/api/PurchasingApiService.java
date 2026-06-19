package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.ImportOrder;
import com.ptithcm.lottemart.data.models.Supplier;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PurchasingApiService {
    @GET("/api/v1/purchasing/admin/suppliers")
    Call<ApiResponse<List<Supplier>>> getSuppliers(@Header("Authorization") String token);

    @GET("/api/v1/purchasing/admin/import-orders")
    Call<ApiResponse<List<ImportOrder>>> getImportOrders(@Header("Authorization") String token);

    @POST("/api/v1/purchasing/admin/import-orders")
    Call<ApiResponse<ImportOrder>> createImportOrder(@Header("Authorization") String token, @Body CreateImportOrderRequest request);

    @PUT("/api/v1/purchasing/admin/import-orders/{id}/receive")
    Call<ApiResponse<Void>> receiveImportOrder(@Header("Authorization") String token, @Path("id") String id);

    class CreateImportOrderRequest {
        public String supplier_id;
        public String branch_id;
        public String note;
        public List<CreateImportOrderItem> items;

        public CreateImportOrderRequest(String supplier_id, String branch_id, String note, List<CreateImportOrderItem> items) {
            this.supplier_id = supplier_id;
            this.branch_id = branch_id;
            this.note = note;
            this.items = items;
        }
    }

    class CreateImportOrderItem {
        public String product_id;
        public String product_name;
        public int quantity_ordered;
        public double unit_cost;

        public CreateImportOrderItem(String product_id, String product_name, int quantity_ordered, double unit_cost) {
            this.product_id = product_id;
            this.product_name = product_name;
            this.quantity_ordered = quantity_ordered;
            this.unit_cost = unit_cost;
        }
    }
}
