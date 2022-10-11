package com.adventour.android;

import java.util.ArrayList;

public class PreviousAdventourModel
{
    private ArrayList<String> locations;
    private String date;

    public PreviousAdventourModel(String date, ArrayList<String> locations)
    {
        this.date = date;
        this.locations = locations;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }

    public String getDate() {
        return date;
    }

    public void setLocations(ArrayList<String> locations) {
        this.locations = locations;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
