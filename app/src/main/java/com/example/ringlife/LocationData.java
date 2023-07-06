package com.example.ringlife;

import android.app.Application;

public class LocationData extends Application {
    // Variabili utili
    private float speedKmh = 0;
    private String address = "";
    private double latitude = 0.00, longitude = 0.00;
    public float getVelocita() {
        return speedKmh;
    }
    public void setVelocita(float speedKmh) {
        this.speedKmh = speedKmh;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
