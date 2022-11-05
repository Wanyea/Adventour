package com.adventour.android;

import androidx.collection.ArraySet;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GlobalVars
{
    // Clear these after each Adventour.
    public static ArrayList<String> exclude = new ArrayList<String>();
    public static ArrayList<String> adventourFSQIds = new ArrayList<String>();
    public static ArrayList<InProgressModel> inProgressModelArrayList = new ArrayList<InProgressModel>();
    public static ArrayList<AdventourSummaryModel> adventourLocations = new ArrayList<AdventourSummaryModel>();
    public static ArrayList<BeaconPostModel> beaconModelArrayList = new ArrayList<BeaconPostModel>();
    public static String selectedLocation = "";
    public static String selectedLocationID = "";
    public static LatLng locationCoordinates;
}
