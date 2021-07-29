package com.example.user.mobilepossystem;

public class Kitchen {
    String table;
    String uid;
    String date;
    String extra_note;
    public Kitchen(){}

    public Kitchen(String table, String uid, String se,String extra_note) {
        this.table = table;
        this.uid = uid;
        this.date=se;
        this.extra_note=extra_note;

    }

    public String getExtra_note() {
        return extra_note;
    }

    public void setExtra_note(String extra_note) {
        this.extra_note = extra_note;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
