package com.adventour.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.Timestamp;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class BeaconsModel {

    private String beaconTitle;
    private String beaconIntro;
    private String beaconAuthor;
    private Bitmap beaconBitmap;
    private String dateCreated;
    private int profilePicReference;


    public BeaconsModel(Map allData, String userNickname, int profilePicReference) {

        this.beaconTitle = (String) allData.get("title");
        this.beaconIntro = (String) allData.get("intro");
        this.beaconAuthor = userNickname;

        try {
            JSONArray locations = (JSONArray) allData.get("locations");
            JSONArray photos = (JSONArray) locations.getJSONObject(0).get("photos");
            String prefix = photos.getJSONObject(0).get("prefix").toString();
            String suffix = photos.getJSONObject(0).get("suffix").toString();

            URL imageURL = new URL(prefix + "original" + suffix);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            this.beaconBitmap = BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.dateCreated = AdventourUtils.formatBirthdateFromDatabase((Timestamp) allData.get("dateCreated"));
        this.profilePicReference = profilePicReference;

        Log.d("Model", dateCreated);
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getBeaconTitle() {
        return beaconTitle;
    }

    public void setBeaconTitle(String beaconTitle) {
        this.beaconTitle = beaconTitle;
    }

    public String getBeaconIntro() {
        return beaconIntro;
    }

    public void setBeaconIntro(String beaconIntro) {
        this.beaconIntro = beaconIntro;
    }

    public String getBeaconAuthor() {
        return beaconAuthor;
    }

    public void setBeaconAuthor(String beaconAuthor) {
        this.beaconAuthor = beaconAuthor;
    }

    public Bitmap getBeaconBitmap() {
        return beaconBitmap;
    }

    public void setBeaconBitmap(Bitmap beaconBitmap) {
        this.beaconBitmap = beaconBitmap;
    }

    public int getProfilePicReference() {
        return profilePicReference;
    }

    public void setProfilePicReference(int profilePicReference) {
        this.profilePicReference = profilePicReference;
    }
}
