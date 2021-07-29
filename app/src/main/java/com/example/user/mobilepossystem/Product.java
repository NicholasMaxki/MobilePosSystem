package com.example.user.mobilepossystem;

public class Product {
    String barcode;
    String product_name;
    String price;
    String quantity;
    String category;
    public Product(){
    }
    public Product(String barcode, String product_name,String category,String price,String quantity) {
        this.barcode = barcode;
        this.product_name = product_name;
        this.category = category;
        this.price=price;
        this.category = category;
        this.quantity = quantity;
    }

    public Product(String product_name, String price, String quantity) {
        this.product_name = product_name;
        this.price = price;
        this.quantity = quantity;
    }


    public void setCategory(String category) {
        this.category = category;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public String getCategory() { return category; }
    public String getBarcode() {
        return barcode;
    }
    public String getProduct_name() {
        return product_name;
    }
    public String getQuantity() {
        return quantity;
    }
    public String getPrice() {
        return price;
    }
}
