package com.example.chargingstation;

/**
 * This class represents one charging station, and holds all the attributes of a single station.
 * Contains basic setters and getters for all class attributes
 */
public class ChargingStation {

    private String title;
    private double longitude;
    private double latitude;
    private String phone;
    private long id;

    /**
     * Initial constructor for a station
     * @param title The name of the station
     * @param longitude The longitude of the station
     * @param latitude The latitude of the station
     * @param phone The phone number of the station
     */
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
    public double getLongitude(){
        return longitude;
    }

    public double getLatitude() {
        return latitude;
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
