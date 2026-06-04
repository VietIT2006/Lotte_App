package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Promotion implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("banner_image")
    private String bannerImage;

    @SerializedName("type")
    private String type;

    @SerializedName("is_active")
    private boolean isActive;

    public Promotion(String title, String description, String bannerImage, String type, boolean isActive) {
        this.title = title;
        this.description = description;
        this.bannerImage = bannerImage;
        this.type = type;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getBannerImage() { return bannerImage; }
    public String getType() { return type; }
    public boolean isActive() { return isActive; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setBannerImage(String bannerImage) { this.bannerImage = bannerImage; }
    public void setType(String type) { this.type = type; }
    public void setActive(boolean active) { isActive = active; }
}
