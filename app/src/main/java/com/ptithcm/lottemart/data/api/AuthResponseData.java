package com.ptithcm.lottemart.data.api;

import com.google.gson.annotations.SerializedName;
import com.ptithcm.lottemart.data.models.User;

public class AuthResponseData {
    @SerializedName("user")
    private User user;
    
    @SerializedName("token")
    private String token;

    public User getUser() { return user; }
    public String getToken() { return token; }
}
