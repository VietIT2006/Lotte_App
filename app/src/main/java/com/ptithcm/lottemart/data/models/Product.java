package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Product implements Serializable {
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
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @SerializedName("brand")
    private String brand;
    @SerializedName("origin")
    private String origin;
    @SerializedName("unit")
    private String unit;
    @SerializedName("rating")
    private double rating;
    @SerializedName("review_count")
    private int reviewCount;
    @SerializedName("sold_count")
    private int soldCount;
    @SerializedName("stock")
    private int stock;
    @SerializedName("highlights")
    private java.util.List<String> highlights;
    @SerializedName("gallery")
    private java.util.List<String> gallery;
    @SerializedName("specifications")
    private java.util.List<Specification> specifications;

    public static class Specification implements Serializable {
        @SerializedName("label")
        private String label;
        @SerializedName("value")
        private String value;

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public int getSoldCount() { return soldCount; }
    public void setSoldCount(int soldCount) { this.soldCount = soldCount; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public java.util.List<String> getHighlights() { return highlights; }
    public void setHighlights(java.util.List<String> highlights) { this.highlights = highlights; }
    public java.util.List<String> getGallery() { return gallery; }
    public void setGallery(java.util.List<String> gallery) { this.gallery = gallery; }
    public java.util.List<Specification> getSpecifications() { return specifications; }
    public void setSpecifications(java.util.List<Specification> specifications) { this.specifications = specifications; }
}

