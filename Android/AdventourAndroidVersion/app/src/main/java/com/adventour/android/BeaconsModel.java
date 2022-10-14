package com.adventour.android;

public class BeaconsModel {
    private String locations;
    private String date;
    private String imageURL;

    public BeaconsModel (String locations, String date, String imageURL) {
        this.locations = locations;
        this.date = date;
        this.imageURL = imageURL;
    }

    public String getLocations() {
        return locations;
    }

    public String getDate() {
        return date;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
