package com.ptithcm.lottemart.data.models;

import java.io.Serializable;

public class ShipperOrder implements Serializable {
    private String id;
    private String status;
    private String branch_name;
    private String branch_address;
    private double total_amount;
    private Object order_address; // In real app, create an Address class to parse JSON
    private String created_at;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBranchName() { return branch_name; }
    public void setBranchName(String branch_name) { this.branch_name = branch_name; }

    public String getBranchAddress() { return branch_address; }
    public void setBranchAddress(String branch_address) { this.branch_address = branch_address; }

    public double getTotalAmount() { return total_amount; }
    public void setTotalAmount(double total_amount) { this.total_amount = total_amount; }

    public String getCreatedAt() { return created_at; }
    public void setCreatedAt(String created_at) { this.created_at = created_at; }
}
