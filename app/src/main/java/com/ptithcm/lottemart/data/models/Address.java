package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Address implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("street")
    private String street;

    @SerializedName("ward")
    private String ward;

    @SerializedName("district")
    private String district;

    @SerializedName("city")
    private String city;

    @SerializedName("full_address")
    private String fullAddress;

    @SerializedName("is_default")
    private boolean isDefault;

    @SerializedName("label")
    private String label;

    public Address(String id, String name, String phone, String street, String ward, String district, String city, boolean isDefault, String label) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.isDefault = isDefault;
        this.label = label;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getStreet() { return street; }
    public String getWard() { return ward; }
    public String getDistrict() { return district; }
    public String getCity() { return city; }
    public String getFullAddress() { 
        if (fullAddress != null && !fullAddress.isEmpty()) {
            return fullAddress;
        }
        return street + ", " + ward + ", " + district + ", " + city;
    }
    public boolean isDefault() { return isDefault; }
    public String getLabel() { return label; }

    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
}
