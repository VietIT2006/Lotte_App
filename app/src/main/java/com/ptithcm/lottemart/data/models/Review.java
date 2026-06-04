package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Review implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("user_name")
    private String userName;
    
    @SerializedName("product_id")
    private String productId;
    
    @SerializedName("product_name")
    private String productName;
    
    @SerializedName("rating")
    private float rating;
    
    @SerializedName("comment")
    private String comment;
    
    @SerializedName("created_at")
    private String createdAt;

    public Review(String id, String userId, String userName, String productId, String productName, float rating, String comment, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.productId = productId;
        this.productName = productName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}
