package com.adventour.android;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.Timestamp;

public class BeaconPostModel {
    private String name;
    private float rating;
    private String address;
    private String description;
    private LocationImages locationImages;

    public BeaconPostModel(String name, float rating, String address, String description, LocationImages locationImages) {
        this.name = name;
        this.rating = rating;
        this.address = address;
        this.description = description;
        this.locationImages = locationImages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationImages getLocationImages() {
        return locationImages;
    }

    public void setLocationImages(LocationImages locationImages) {
        this.locationImages = locationImages;
    }
}
