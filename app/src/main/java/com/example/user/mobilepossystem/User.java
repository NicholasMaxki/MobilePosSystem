package com.example.user.mobilepossystem;

public class User {
    String user_id;
    String user_full_name;
    String user_phone_number;
    String user_password;
    String  user_shop_name;

    public User(){
    }

    public User(String user_full_name, String user_phone_number, String user_password,String user_shop_name) {
        this.user_full_name = user_full_name;
        this.user_phone_number = user_phone_number;
        this.user_password = user_password;
        this.user_shop_name = user_shop_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public void setUser_phone_number(String user_phone_number) {
        this.user_phone_number = user_phone_number;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_shop_name() {
        return user_shop_name;
    }

    public void setUser_shop_name(String user_shop_name) {
        this.user_shop_name = user_shop_name;
    }
}
