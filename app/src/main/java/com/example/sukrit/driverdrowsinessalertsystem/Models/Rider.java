package com.example.sukrit.driverdrowsinessalertsystem.Models;

import java.util.ArrayList;

/**
 * Created by sharaddadhich on 24/04/18.
 */

public class Rider {

    String name,email,password,mobNo;

    public Rider(){}

    public Rider(String name, String email, String password, String mobNo) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobNo = mobNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }
}
