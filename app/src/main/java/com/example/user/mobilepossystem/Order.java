package com.example.user.mobilepossystem;

public class Order {
    String product_name;
    String price;
    String quantity;
    public Order(){}

    public Order(String product_name, String price, String quantity) {
        this.product_name = product_name;
        this.price = price;
        this.quantity = quantity;
    }
    public Order(String product_name, String quantity) {
        this.product_name = product_name;
        this.quantity = quantity;
    }
    public String getProduct_name() {
        return product_name;
    }
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
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
}
