package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("full_name")
    private String fullName;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("role_key")
    private String role;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("lotte_points")
    private int lottePoints;
    
    @SerializedName("membership_level")
    private String membershipLevel;

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getAvatar() { return avatar; }
    public int getLottePoints() { return lottePoints; }
    public String getMembershipLevel() { return membershipLevel; }
}
