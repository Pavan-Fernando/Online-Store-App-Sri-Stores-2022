package com.mad.sristores.model;

public class Cart {

    private String productId;
    private String productName;
    private String price;
    private String quantity;
    private String discount;

    public Cart(String producrtId, String productName, String price, String quantity, String discount) {
        this.productId = producrtId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }

    public Cart() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
