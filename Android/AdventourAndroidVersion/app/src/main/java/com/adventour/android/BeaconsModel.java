package com.adventour.android;

import android.util.Log;

import com.google.firebase.Timestamp;

import org.json.JSONArray;

import java.util.Map;

public class BeaconsModel {

    private String firstLocation;
    private String secondLocation;
    private String thirdLocation;
    private String dateCreated;


    public BeaconsModel(Map allData)
    {
        JSONArray array = (JSONArray) allData.get("locations");
        if (array.length() == 1)
        {
            try {
                this.firstLocation = array.getJSONObject(0).get("name").toString();
                this.secondLocation = "";
                this.thirdLocation = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (array.length() == 2)
        {
            try {
                this.firstLocation = array.getJSONObject(0).get("name").toString();
                this.secondLocation = array.getJSONObject(1).get("name").toString();
                this.thirdLocation = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (array.length() > 2)
        {
            try {
                this.firstLocation = array.getJSONObject(0).get("name").toString();
                this.secondLocation = array.getJSONObject(1).get("name").toString();
                this.thirdLocation = array.getJSONObject(2).get("name").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        this.dateCreated = AdventourUtils.formatBirthdateFromDatabase((Timestamp) allData.get("dateCreated"));

        Log.d("Model", dateCreated);
    }

    public String getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(String firstLocation) {
        this.firstLocation = firstLocation;
    }

    public String getSecondLocation() {
        return secondLocation;
    }

    public void setSecondLocation(String secondLocation) {
        this.secondLocation = secondLocation;
    }

    public String getThirdLocation() {
        return thirdLocation;
    }

    public void setThirdLocation(String thirdLocation) {
        this.thirdLocation = thirdLocation;
    }
}
