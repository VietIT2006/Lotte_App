package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.Order;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderApiService {
    @GET("/api/v1/ordering/admin/orders")
    Call<ApiResponse<List<Order>>> getAdminOrders(@Header("Authorization") String token);

    @GET("/api/v1/ordering/history")
    Call<ApiResponse<List<Order>>> getMyOrders(@Header("Authorization") String token);

    @PUT("/api/v1/ordering/admin/orders/{id}/status")
    Call<ApiResponse<Order>> updateOrderStatus(
            @Header("Authorization") String token,
            @Path("id") String orderId,
            @Body UpdateOrderStatusRequest request
    );

    @POST("/api/v1/ordering/checkout")
    Call<ApiResponse<Order>> checkout(
            @Header("Authorization") String token,
            @Body CheckoutRequest request
    );

    @GET("/api/v1/ordering/cart")
    Call<ApiResponse<CartResponse>> getCart(@Header("Authorization") String token);

    @POST("/api/v1/ordering/cart")
    Call<ApiResponse<CartResponse>> addToCart(@Header("Authorization") String token, @Body AddToCartRequest request);

    @PUT("/api/v1/ordering/cart")
    Call<ApiResponse<CartResponse>> updateCartQty(@Header("Authorization") String token, @Body UpdateCartQtyRequest request);

    @DELETE("/api/v1/ordering/cart/{branch_product_id}")
    Call<ApiResponse<CartResponse>> removeFromCart(@Header("Authorization") String token, @Path("branch_product_id") String branchProductId);

    class UpdateCartQtyRequest {
        private String branch_product_id;
        private int quantity;

        public UpdateCartQtyRequest(String branch_product_id, int quantity) {
            this.branch_product_id = branch_product_id;
            this.quantity = quantity;
        }
    }

    class UpdateOrderStatusRequest {
        private String status;
        public UpdateOrderStatusRequest(String status) {
            this.status = status;
        }
    }

    class AddToCartRequest {
        private String branch_product_id;
        private String product_name;
        private String product_image;
        private int quantity;
        private double price;
        private double unit_price;

        public AddToCartRequest(String branch_product_id, String product_name, String product_image, int quantity, double price) {
            this.branch_product_id = branch_product_id;
            this.product_name = product_name;
            this.product_image = product_image;
            this.quantity = quantity;
            this.price = price;
            this.unit_price = price;
        }
    }

    class CartResponse {
        private String id;
        private List<CartItemResponse> items;
        public List<CartItemResponse> getItems() { return items; }
    }

    class CartItemResponse {
        private String id;
        private int quantity;
        private com.ptithcm.lottemart.data.models.Product product;

        public com.ptithcm.lottemart.data.models.CartItem toCartItem() {
            return new com.ptithcm.lottemart.data.models.CartItem(id, product, quantity);
        }
    }

    class CheckoutRequest {
        private List<CheckoutItem> items;
        private double total_amount;
        private double shipping_fee;
        private String payment_method;

        public CheckoutRequest(List<CheckoutItem> items, double total_amount, double shipping_fee, String payment_method) {
            this.items = items;
            this.total_amount = total_amount;
            this.shipping_fee = shipping_fee;
            this.payment_method = payment_method;
        }
    }

    class CheckoutItem {
        private String branch_product_id;
        private String product_name;
        private String product_image;
        private int quantity;
        private double price;

        public CheckoutItem(String branch_product_id, String product_name, String product_image, int quantity, double price) {
            this.branch_product_id = branch_product_id;
            this.product_name = product_name;
            this.product_image = product_image;
            this.quantity = quantity;
            this.price = price;
        }
    }
}

