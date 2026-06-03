package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;

public class Branch {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("city")
    private String city;

    public Branch(String id, String name, String address, String city) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
}
