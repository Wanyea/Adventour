package com.adventour.android;

public class BeaconPostModel {

    private String name;
    private float rating;
    private String address;
    private String description;

    public BeaconPostModel(String name, float rating, String address, String description) {
        this.name = name;
        this.rating = rating;
        this.address = address;
        this.description = description;
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

}
