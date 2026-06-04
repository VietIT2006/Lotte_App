package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class StockMovement implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("branch_id")
    private String branchId;

    @SerializedName("branch_name")
    private String branchName;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("batch_code")
    private String batchCode;

    @SerializedName("movement_type")
    private String movementType;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("before_stock")
    private int beforeStock;

    @SerializedName("after_stock")
    private int afterStock;

    @SerializedName("note")
    private String note;

    public String getId() { return id; }
    public String getBranchId() { return branchId; }
    public String getBranchName() { return branchName; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getBatchCode() { return batchCode; }
    public String getMovementType() { return movementType; }
    public int getQuantity() { return quantity; }
    public int getBeforeStock() { return beforeStock; }
    public int getAfterStock() { return afterStock; }
    public String getNote() { return note; }
}
