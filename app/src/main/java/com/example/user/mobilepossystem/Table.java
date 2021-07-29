package com.example.user.mobilepossystem;

public class Table {
    String table;
    String status;
    String pax;
    public Table(){}

    public Table(String table, String status, String pax) {
        this.table = table;
        this.status=status;
        this.pax=pax;
    }

    public String getPax() {
        return pax;
    }

    public void setPax(String pax) {
        this.pax = pax;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
