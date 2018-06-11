package com.coen268.tripmate.models;

public class TravelPlan {

    private String tripId;

    private String tripName;

    private String color;


    public TravelPlan() { this.color = "#a3a0a0"; }

    public TravelPlan(String tripName, String tripId){
        this.tripName=tripName;
        this.tripId = tripId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
