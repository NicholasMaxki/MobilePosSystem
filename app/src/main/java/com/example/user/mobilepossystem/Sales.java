package com.example.user.mobilepossystem;

public class Sales {
    String year;
    String month;
    String total;

    Sales(){}

    public Sales(String year, String month, String total) {
        this.year = year;
        this.month = month;
        this.total = total;
    }
    public Sales(String year, String total) {
        this.year = year;
        this.total = total;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
