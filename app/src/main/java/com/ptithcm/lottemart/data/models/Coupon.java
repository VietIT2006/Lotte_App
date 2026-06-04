package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Coupon implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("code")
    private String code;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("discount_value")
    private double discountValue;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("is_active")
    private boolean isActive;

    public Coupon(String code, String title, double discountValue, String image, boolean isActive) {
        this.code = code;
        this.title = title;
        this.discountValue = discountValue;
        this.image = image;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public double getDiscountValue() { return discountValue; }
    public String getImage() { return image; }
    public boolean isActive() { return isActive; }
}
