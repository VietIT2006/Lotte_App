package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class ImportOrder implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("order_code")
    private String orderCode;
    @SerializedName("supplier_id")
    private String supplierId;
    @SerializedName("supplier_name")
    private String supplierName;
    @SerializedName("status")
    private String status;
    @SerializedName("total_amount")
    private double totalAmount;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("items")
    private List<ImportOrderItem> items;

    public String getId() { return id; }
    public String getOrderCode() { return orderCode; }
    public String getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public String getCreatedAt() { return createdAt; }
    public List<ImportOrderItem> getItems() { return items; }
}
