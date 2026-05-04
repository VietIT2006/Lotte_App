package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("price")
    private double price;
    
    @SerializedName("original_price")
    private double originalPrice;
    
    @SerializedName("thumbnail")
    private String imageUrl;
    
    @SerializedName("description")
    private String description;

    public Product(String id, String name, double price, double originalPrice, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
}
