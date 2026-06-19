package com.ptithcm.lottemart.data.api;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("password")
    private String password;
    @SerializedName("full_name")
    private String fullName;

    public RegisterRequest(String username, String email, String phone, String password, String fullName) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.fullName = fullName;
    }
}
