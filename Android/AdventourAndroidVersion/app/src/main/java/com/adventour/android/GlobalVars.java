package com.adventour.android;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.ArrayList;

public class GlobalVars
{
    public static boolean adventourInProgress = false;

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
    public static ArrayList<String> locationDescriptions = new ArrayList<String>();
    public static ArrayList<BeaconsModel> beaconBoardArrayList = new ArrayList<BeaconsModel>();
    public static String lastLocationCoordinates = "";
    public static String lastLocationName = "";

    // For previous adventours in passport
    public static ArrayList<AdventourSummaryModel> adventourLocationsPassport = new ArrayList<AdventourSummaryModel>();
    public static ArrayList<BeaconPostModel> beaconModelArrayListPassport = new ArrayList<BeaconPostModel>();
    public static ArrayList<String> adventourFSQIdsPassport = new ArrayList<String>();
    public static String selectedLocationPassport = "";
    public static ArrayList<String> locationDescriptionsPassport = new ArrayList<String>();

    // For the Beacon Board
    public static ArrayList<BeaconPostModel> beaconModelArrayListBeaconBoard = new ArrayList<BeaconPostModel>();
    public static ArrayList<String> adventourFSQIdsBeaconBoard = new ArrayList<String>();
    public static ArrayList<String> locationDescriptionsBeaconBoard = new ArrayList<String>();
}
