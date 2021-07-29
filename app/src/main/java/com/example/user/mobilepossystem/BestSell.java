package com.example.user.mobilepossystem;

public class BestSell {
    String product_name;
    String quantity;

    public BestSell(){}

    public BestSell(String product_name, String quantity) {
        this.product_name = product_name;
        this.quantity = quantity;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
