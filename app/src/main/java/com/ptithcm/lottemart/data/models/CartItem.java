package com.ptithcm.lottemart.data.models;

/**
 * Model class for items in the shopping cart.
 */
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
    public void setId(String id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
