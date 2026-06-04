package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ImportOrderItem implements Serializable {
    @SerializedName("product_id")
    private String productId;
    @SerializedName("product_name")
    private String productName;
    @SerializedName("quantity_ordered")
    private int quantityOrdered;
    @SerializedName("unit_cost")
    private double unitCost;
    @SerializedName("subtotal")
    private double subtotal;

    public ImportOrderItem(String productId, String productName, int quantityOrdered, double unitCost) {
        this.productId = productId;
        this.productName = productName;
        this.quantityOrdered = quantityOrdered;
        this.unitCost = unitCost;
        this.subtotal = quantityOrdered * unitCost;
    }

    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantityOrdered() { return quantityOrdered; }
    public double getUnitCost() { return unitCost; }
    public double getSubtotal() { return subtotal; }
}
