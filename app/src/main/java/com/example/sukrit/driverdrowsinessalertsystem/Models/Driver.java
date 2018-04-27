package com.example.sukrit.driverdrowsinessalertsystem.Models;

import java.util.ArrayList;

/**
 * Created by sharaddadhich on 24/04/18.
 */

public class Driver {

    String name,email,vehicleNo,password,mobNo;
    ArrayList<DriverPastRide> driverPastRides;
    DriverCurrentRide driverCurrentRide;

    public Driver(){}

    public Driver(String name, String email, String vehicleNo, String password, String mobNo, ArrayList<DriverPastRide> driverPastRides,DriverCurrentRide driverCurrentRide) {
        this.name = name;
        this.email = email;
        this.vehicleNo = vehicleNo;
        this.password = password;
        this.mobNo = mobNo;
        this.driverPastRides = driverPastRides;
        this.driverCurrentRide = driverCurrentRide;

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

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
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

    public ArrayList<DriverPastRide> getDriverPastRides() {
        return driverPastRides;
    }

    public void setDriverPastRides(ArrayList<DriverPastRide> driverPastRides) {
        this.driverPastRides = driverPastRides;
    }

    public DriverCurrentRide getDriverCurrentRide() {
        return driverCurrentRide;
    }

    public void setDriverCurrentRide(DriverCurrentRide driverCurrentRide) {
        this.driverCurrentRide = driverCurrentRide;
    }
}
