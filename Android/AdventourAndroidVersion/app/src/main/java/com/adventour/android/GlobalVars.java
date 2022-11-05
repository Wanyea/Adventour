package com.adventour.android;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.ArrayList;

public class GlobalVars
{
    // Clear these after each Adventour.
    public static JSONArray excludes = new JSONArray();
    public static ArrayList<String> adventourFSQIds = new ArrayList<String>();
    public static ArrayList<InProgressModel> inProgressModelArrayList = new ArrayList<InProgressModel>();
    public static ArrayList<AdventourSummaryModel> adventourLocations = new ArrayList<AdventourSummaryModel>();
    public static ArrayList<BeaconPostModel> beaconModelArrayList = new ArrayList<BeaconPostModel>();
    public static ArrayList<PreviousAdventourModel> previousAdventourArrayList = new ArrayList<>();
    public static ArrayList<BeaconsModel> userBeaconsArrayList = new ArrayList<BeaconsModel>();
    public static String selectedLocation = "";
    public static String selectedLocationID = "";
    public static LatLng locationCoordinates;
}
