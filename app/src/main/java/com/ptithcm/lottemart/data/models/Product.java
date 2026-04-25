package com.ptithcm.lottemart.data.models;

public class Product {
    private String id;
    private String name;
    private double price;
    private double originalPrice;
    private String imageUrl;

    public Product(String id, String name, double price, double originalPrice, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public String getImageUrl() { return imageUrl; }
}
