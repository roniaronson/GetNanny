package com.example.getnanny20;

public class Post {
    private String userID;
    private String name;
    private int hourlyRate;
    private int age;
    private int yearsOfExperience;
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

    public int getAge() {
        return age;
    }

    public Post setAge(int age) {
        this.age = age;
        return this;
    }

    public int getHourlyRate() {
        return hourlyRate;
    }

    public Post setHourlyRate(int hourlyRate) {
        this.hourlyRate = hourlyRate;
        return this;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public Post setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Post setDescription(String description) {
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
