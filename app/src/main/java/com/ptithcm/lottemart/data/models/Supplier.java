package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Supplier implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;
    @SerializedName("contact_name")
    private String contactName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private String address;
    @SerializedName("is_active")
    private boolean isActive;

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getContactName() { return contactName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public boolean isActive() { return isActive; }
    
    @Override
    public String toString() {
        return name; // For Spinner display
    }
}
