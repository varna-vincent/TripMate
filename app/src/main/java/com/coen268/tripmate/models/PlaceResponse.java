package com.coen268.tripmate.models;

import android.graphics.Bitmap;

import com.google.android.gms.location.places.Place;

public class PlaceResponse {

    private String id;
    private String name;
    private Bitmap image;

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

    @Override
    public String toString() {

        System.out.println("\n\nPlace Name : " + getName()
                + "\nPlace Image : " + image
                + "\nPlace Id : " + getId());

        return "\n\nPlace Name : " + getName()
                + "\nPlace Image : " + image
                + "\nPlace Id : " + getId();
    }
}
