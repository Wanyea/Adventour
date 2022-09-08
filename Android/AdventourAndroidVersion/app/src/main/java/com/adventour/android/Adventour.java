package com.adventour.android;

import java.util.Date;

public class Adventour {

    private int id;
    private int numLocations;

    // We might just do one of these
    private String[] categories;
    private String[] moods;

    // These may be unused, time will tell
    private Location[] locations;
    private boolean isPrivate;
    private boolean isBeacon;

    private String dateCreated;

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String[] getMoods() {
        return moods;
    }

    public void setMoods(String[] moods) {
        this.moods = moods;
    }

    private String dateUpdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumLocations() {
        return numLocations;
    }

    public void setNumLocations(int numLocations) {
        this.numLocations = numLocations;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isBeacon() {
        return isBeacon;
    }

    public void setBeacon(boolean isBeacon) {
        this.isBeacon = isBeacon;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
