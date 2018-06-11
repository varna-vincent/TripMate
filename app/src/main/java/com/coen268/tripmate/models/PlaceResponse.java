package com.coen268.tripmate.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;

public class PlaceResponse implements Parcelable {

    private String id;
    private String name;
    private Bitmap image;
    private String latitude;
    private String longitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {

        System.out.println("\n\nPlace Name : " + getName()
                + "\nPlace Image : " + image
                + "\nPlace Id : " + getId());

        return "\n\nPlace Name : " + getName()
                + "\nPlace Image : " + image
                + "\nPlace Id : " + getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
