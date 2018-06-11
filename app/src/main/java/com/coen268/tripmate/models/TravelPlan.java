package com.coen268.tripmate.models;

import android.os.Parcelable;

import java.io.Serializable;

public class TravelPlan implements Serializable {

    private String tripId;

    private String tripName;

    private int color;


    public TravelPlan() { this.color = -5052532; }

    public TravelPlan(String tripName){
        this.tripName=tripName;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
