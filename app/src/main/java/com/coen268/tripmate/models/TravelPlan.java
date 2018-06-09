package com.coen268.tripmate.models;

public class TravelPlan {
    public String getTripName() {
        return tripName;
    }

    private String tripName;

    public String getTripId() {
        return tripId;
    }


    private String tripId;
    public TravelPlan(String tripName, String tripId){
        this.tripName=tripName;
        this.tripId = tripId;

    }


}
