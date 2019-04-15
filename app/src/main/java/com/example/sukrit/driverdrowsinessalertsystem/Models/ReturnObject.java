package com.example.sukrit.driverdrowsinessalertsystem.Models;

/**
 * Created by sukrit on 16/4/19.
 */

public class ReturnObject {
    String nlr,mor,ear;

    public ReturnObject() {
    }

    public ReturnObject(String nlr, String mor, String ear) {
        this.nlr = nlr;
        this.mor = mor;
        this.ear = ear;
    }

    public String getNlr() {
        return nlr;
    }

    public void setNlr(String nlr) {
        this.nlr = nlr;
    }

    public String getMor() {
        return mor;
    }

    public void setMor(String mor) {
        this.mor = mor;
    }

    public String getEar() {
        return ear;
    }

    public void setEar(String ear) {
        this.ear = ear;
    }
}
