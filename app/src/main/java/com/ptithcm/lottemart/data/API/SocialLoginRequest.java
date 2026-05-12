package com.ptithcm.lottemart.data.api;

import com.google.gson.annotations.SerializedName;

public class SocialLoginRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("provider")
    private String provider; // "google" or "facebook"

    @SerializedName("provider_id")
    private String providerId;

    public SocialLoginRequest(String email, String fullName, String avatar, String provider, String providerId) {
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
        this.provider = provider;
        this.providerId = providerId;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
}
