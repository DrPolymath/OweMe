package com.example.oweme.models;

import java.util.ArrayList;

public class User {
    private String name;
    private String phonenumber;
    private String deviceToken;

    public User() {
    }

    public User(String name, String phonenumber, String deviceToken) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.deviceToken = deviceToken;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
