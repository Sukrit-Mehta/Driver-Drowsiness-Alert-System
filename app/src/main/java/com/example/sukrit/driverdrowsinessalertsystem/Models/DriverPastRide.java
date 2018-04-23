package com.example.sukrit.driverdrowsinessalertsystem.Models;

/**
 * Created by sukrit on 13/4/18.
 */

public class DriverPastRide {
    String source;
    String destination;
    String driverID;
    Integer sleepCount;
    Double avgSpeed;
    Double startLat;
    Double startLng;
    Double endLat;
    Double endLng;
    String startTime;
    String endTime;
    String date;
    Double rating;

    public DriverPastRide() {
    }

    public DriverPastRide(String source, String destination, String driverID, Integer sleepCount, Double avgSpeed, Double startLat, Double startLng, Double endLat, Double endLng, String startTime, String endTime, String date, Double rating) {
        this.source = source;
        this.destination = destination;
        this.driverID = driverID;
        this.sleepCount = sleepCount;
        this.avgSpeed = avgSpeed;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.rating = rating;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public Integer getSleepCount() {
        return sleepCount;
    }

    public void setSleepCount(Integer sleepCount) {
        this.sleepCount = sleepCount;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLng() {
        return startLng;
    }

    public void setStartLng(Double startLng) {
        this.startLng = startLng;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLng() {
        return endLng;
    }

    public void setEndLng(Double endLng) {
        this.endLng = endLng;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
