package com.ptithcm.lottemart.data.models;

public class Banner {
    private String id;
    private String title;
    private String summary;
    private String imageUrl; // Maps to "banner" or "thumbnail" in JSON

    public Banner(String id, String title, String summary, String imageUrl) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getImageUrl() { return imageUrl; }
}
