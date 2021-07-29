package com.example.user.mobilepossystem;

public class Item {
    String product_name;
    String price;
    String category;
    String quantity;
    String sequence;
    String table;
    public Item(){}

    public Item(String product_name, String price, String category) {
        this.product_name = product_name;
        this.price = price;
        this.category = category;
    }
    public Item(String product_name, String price, String quantity,String sequence, String table) {
        this.product_name = product_name;
        this.price = price;
        this.quantity=quantity;
    }
    public Item(String product_name, String price) {
        this.product_name = product_name;
        this.price = price;
    }
    public Item(String product_name, String price, String quantity, String table) {
        this.product_name = product_name;
        this.price = price;
        this.quantity=quantity;
        this.table=table;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
