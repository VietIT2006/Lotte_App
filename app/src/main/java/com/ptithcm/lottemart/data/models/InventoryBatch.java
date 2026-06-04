package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class InventoryBatch implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("batch_code")
    private String batchCode;
    @SerializedName("product_name")
    private String productName;
    @SerializedName("supplier_name")
    private String supplierName;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("original_quantity")
    private int originalQuantity;
    @SerializedName("cost_price")
    private double costPrice;
    @SerializedName("received_date")
    private String receivedDate;
    @SerializedName("exp_date")
    private String expDate;

    public String getId() { return id; }
    public String getBatchCode() { return batchCode; }
    public String getProductName() { return productName; }
    public String getSupplierName() { return supplierName; }
    public int getQuantity() { return quantity; }
    public int getOriginalQuantity() { return originalQuantity; }
    public double getCostPrice() { return costPrice; }
    public String getReceivedDate() { return receivedDate; }
    public String getExpDate() { return expDate; }
}
