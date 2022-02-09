package com.example.getnanny20;

public class Post {
    private String userID;
    private String name;
    private String location;
    private int hourlyRate;
    private String description;
    private String image;
    private double lat;
    private double lon;

    public Post() {
    }

    public String getUserID() {
        return userID;
    }

    public Post setUserID(String userID) {
        this.userID = userID;
        return this;
    }


    public String getName() {
        return name;
    }

    public Post setName(String name) {
        this.name = name;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Post setLocation(String location) {
        this.location = location;
        return this;
    }

    public int getHourlyRate() {
        return hourlyRate;
    }

    public Post setAmount(int hourlyRate) {
        this.hourlyRate = hourlyRate;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Post setDateString(String description) {
        this.description = description;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Post setImage(String image) {
        this.image = image;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public Post setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLon() {
        return lon;
    }

    public Post setLon(double lon) {
        this.lon = lon;
        return this;
    }

   //TO DO
    @Override
    public String toString() {
        return "to do";
    }
}
