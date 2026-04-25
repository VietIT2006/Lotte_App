package com.ptithcm.lottemart.data.models;

public class CartItem {
    private String id;
    private Product product;
    private int quantity;

    public CartItem(String id, Product product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
}
