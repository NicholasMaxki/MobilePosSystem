package com.example.user.mobilepossystem;

public class Transaction {
    String month;
    String year;
    String order_id;
    String date_time;
    String payment_type;
    String sub_total;
    String total_amount;
    String discount;
    String discount_rate;
    String change;
    String cash;
    String pid;
    String table;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String date;
    public Transaction() { }

    public Transaction(String month, String year, String order_id, String date_time, String payment_type, String sub_total, String total_amount, String discount, String discount_rate, String change, String cash,String date,String pid) {
        this.month = month;
        this.year = year;
        this.order_id = order_id;
        this.date_time = date_time;
        this.payment_type = payment_type;
        this.sub_total = sub_total;
        this.total_amount = total_amount;
        this.discount = discount;
        this.discount_rate = discount_rate;
        this.change = change;
        this.cash = cash;
        this.date=date;
        this.pid=pid;
    }
    public Transaction(String month, String year, String order_id, String date_time, String payment_type, String sub_total, String total_amount, String discount, String discount_rate, String change, String cash,String date,String pid,String table) {
        this.month = month;
        this.year = year;
        this.order_id = order_id;
        this.date_time = date_time;
        this.payment_type = payment_type;
        this.sub_total = sub_total;
        this.total_amount = total_amount;
        this.discount = discount;
        this.discount_rate = discount_rate;
        this.change = change;
        this.cash = cash;
        this.date=date;
        this.pid=pid;
        this.table=table;
    }
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscount_rate() {
        return discount_rate;
    }

    public void setDiscount_rate(String discount_rate) {
        this.discount_rate = discount_rate;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
