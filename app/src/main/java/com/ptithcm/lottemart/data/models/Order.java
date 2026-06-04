package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("status")
    private String status;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("shipping_fee")
    private double shippingFee;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("items")
    private List<OrderItem> items;

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalAmount() { return totalAmount; }
    public double getShippingFee() { return shippingFee; }
    public String getCreatedAt() { return createdAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public List<OrderItem> getItems() { return items; }

    public static class OrderItem implements Serializable {
        @SerializedName("product_name")
        private String productName;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("price")
        private double price;

        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
    }
}
