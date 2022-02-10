package com.example.getnanny20;

public class Post {
    private String userID;
    private String phoneNumber;
    private String name;
    private int hourlyRate;
    private int age;
    private int yearsOfExperience;
    private String description;
    private int numOfChildren;
    private String image;
    private String dateString;
    private double lat;
    private double lon;
    private boolean isParent;

    public Post() {
    }

    public String getUserID() {
        return userID;
    }

    public Post setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Post setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public int getNumOfChildren() {
        return numOfChildren;
    }

    public Post setNumOfChildren(int numOfChildren) {
        this.numOfChildren = numOfChildren;
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

    public String getDateString() {
        return dateString;
    }

    public Post setDateString(String dateString) {
        this.dateString = dateString;
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

    public boolean getIsParent(){return isParent;}

    public Post setIsParent(boolean isParent){
        this.isParent = isParent;
        return this;
    }
   //TO DO
    @Override
    public String toString() {
        return "to do";
    }
}
