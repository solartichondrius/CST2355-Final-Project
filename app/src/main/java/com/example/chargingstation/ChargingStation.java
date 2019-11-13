package com.example.chargingstation;

public class ChargingStation {

    private String title;
    private double longitude;
    private double latitude;
    private String phone;
    private long id;

    public ChargingStation(String title, double longitude, double latitude, String phone){
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phone = phone;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public double getLongitude(){
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
