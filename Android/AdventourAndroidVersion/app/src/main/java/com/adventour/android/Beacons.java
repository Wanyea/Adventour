package com.adventour.android;

import static com.adventour.android.BuildConfig.MAPS_API_KEY;
import static java.lang.Math.toIntExact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Beacons extends AppCompatActivity {


    BeaconsAdapter beaconBoardAdapter;
    int androidPfpRef;
    String nickname;
    String userId;
    ProgressBar beaconsProgressBar;
    AutocompleteSupportFragment autocompleteFragment;
    String selectedBeaconLocation;
    PlacesClient placesClient;
    HashMap<String, String> isSwitchActive = new HashMap<>();
    int distance = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);

        handleAuth();

        // Dump Global Vars
        GlobalVars.previousAdventourArrayList.clear();
        GlobalVars.userBeaconsArrayList.clear();
        GlobalVars.beaconModelArrayListBeaconBoard.clear();
        GlobalVars.adventourFSQIdsBeaconBoard.clear();
        GlobalVars.locationDescriptionsBeaconBoard.clear();

        beaconsProgressBar = (ProgressBar) findViewById(R.id.beaconsProgressBar);

        if (getIntent().getSerializableExtra("isSwitchActive") != null)
        {
            isSwitchActive = (HashMap) getIntent().getSerializableExtra("isSwitchActive");
            Log.d("InProgress isSwitch", isSwitchActive.toString());
        }

        if (getIntent().getSerializableExtra("distance") != null)
        {
            distance = (int) getIntent().getSerializableExtra("distance");
            Log.d("InProgress distance", String.valueOf(distance));
        }

        // Initialize places client
        Places.initialize(this, MAPS_API_KEY);
        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.

        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.beacon_board_autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setCountry("US");
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.OVERLAY);
        autocompleteFragment.setHint("Enter location");


        if (!Objects.equals(GlobalVars.selectedLocation, ""))
        {
            autocompleteFragment.setText(selectedBeaconLocation);
        }

        ((EditText) autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)).setTextSize(18.0f);
        ((EditText) autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)).setHintTextColor(Color.BLACK);

        autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        autocompleteFragment.setText("");
                        view.setVisibility(View.GONE);
                    }
                });

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                selectedBeaconLocation = String.valueOf(place.getAddress());
                getBeacons(selectedBeaconLocation);

                Log.i("Beacons", "Place: " + selectedBeaconLocation);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("Beacons", "An error occurred: " + status);
            }
        });

        RecyclerView beaconsRV = findViewById(R.id.beaconsRV);
        beaconsRV.setNestedScrollingEnabled(false);

        beaconBoardAdapter = new BeaconsAdapter(this, GlobalVars.beaconBoardArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        beaconsRV.setLayoutManager(linearLayoutManager);
        beaconsRV.setAdapter(beaconBoardAdapter);

        beaconsRV.setNestedScrollingEnabled(false);
        beaconsRV.addOnItemTouchListener(
                new BeaconsClickListener(this, beaconsRV, new BeaconsClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("Beacons", "onItemClicked triggered, " + GlobalVars.beaconBoardArrayList.get(position).getAdventourId());
                        switchToBeaconPost(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.d("PreviousAdventourClick", "onLongItemClicked triggered");
                    }
                })
        );

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.beacons);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnItemSelectedListener) item -> {

            switch (item.getItemId()) {
                case R.id.passport:
                    Intent passportIntent = new Intent(getApplicationContext(), Passport.class);
                    passportIntent.putExtra("isSwitchActive", isSwitchActive);
                    passportIntent.putExtra("distance", distance);
                    startActivity(passportIntent);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.start_adventour:
                    Intent startAdventourIntent = new Intent(getApplicationContext(), StartAdventour.class);
                    startAdventourIntent.putExtra("isSwitchActive", isSwitchActive);
                    startAdventourIntent.putExtra("distance", distance);
                    startActivity(startAdventourIntent);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.beacons:
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void switchToLoggedOut() {
        Intent intent = new Intent(this, LoggedOut.class);
        startActivity(intent);
        finish();
    }

    public void handleAuth() {
        FirebaseAuth auth;
        FirebaseUser user;

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            switchToLoggedOut();
        }
    }

    public void switchToBeaconPost(int position) {
        Context c = this;
        final String TAG = "PreparingBeacon...";
        String adventourID = GlobalVars.beaconBoardArrayList.get(position).getAdventourId();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Look up the beacon
        Log.d(TAG, "Looking up Beacon with id: " + adventourID);
        DocumentReference documentRef = db.collection("Beacons").document(adventourID);
        documentRef.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed calling database");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d(TAG, "Completed calling database");
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "Documents retrieved!");

                            DocumentSnapshot documentSnapshot = task.getResult();
                            Log.d(TAG, documentSnapshot.toString());
                            GlobalVars.adventourFSQIdsBeaconBoard = (ArrayList<String>) documentSnapshot.get("locations");
//                            GlobalVars.selectedLocationPassport = documentSnapshot.getString("beaconLocation");
                            GlobalVars.locationDescriptionsBeaconBoard = (ArrayList<String>) documentSnapshot.get("locationDescriptions");
//                            Log.d(TAG, GlobalVars.adventourFSQIds.toString());

                            Log.d(TAG, documentSnapshot.get("numLocations") + " adventourFSQIds:" + GlobalVars.adventourFSQIdsBeaconBoard);

                            JSONArray results = new JSONArray();
                            Map<String, Object> allData = new HashMap<>();
                            JSONObject requestBody = new JSONObject();

                            try
                            {
                                requestBody.put("ids", new JSONArray(GlobalVars.adventourFSQIdsBeaconBoard));
                                requestBody.put("uid", user.getUid());
                            } catch (JSONException e) {
                                Log.e(TAG, e.toString());
                            }

                            try {
                                URL url = new URL("https://adventour-183a0.uc.r.appspot.com/get-foursquare-places");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                                conn.setDoOutput(true);
                                conn.setInstanceFollowRedirects(false);
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type", "application/json");
                                conn.setRequestProperty("Accept", "application/json");
                                conn.setUseCaches(false);

                                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                                wr.writeBytes(requestBody.toString());
                                wr.flush();
                                wr.close();
                                requestBody = null;

                                System.out.println("\nSending 'POST' request to URL : " + url);

                                InputStream it = conn.getInputStream();
                                InputStreamReader inputs = new InputStreamReader(it);

                                BufferedReader in = new BufferedReader(inputs);
                                String inputLine;
                                StringBuffer response = new StringBuffer();

                                while ((inputLine = in.readLine()) != null) {
                                    response.append(inputLine);
                                }

                                in.close();
                                JSONObject responseData = new JSONObject(response.toString());
                                results = (JSONArray) responseData.get("results");

                                for (int i = 0; i < GlobalVars.adventourFSQIdsBeaconBoard.size(); i++) {
                                    JSONObject obj = (JSONObject) results.get(i);
                                    Log.d(TAG, obj.toString());

                                    String description = "";

                                    description = GlobalVars.locationDescriptionsBeaconBoard.get(i);

//                                    GlobalVars.adventourLocationsPassport.add(new AdventourSummaryModel(obj.getString("name"), description));
//                                    Log.d(TAG, "adventourLocations: " + GlobalVars.adventourLocationsPassport.toString());

                                    LocationImages locationImages = getLocationImages(obj.getJSONArray("photos"));
                                    GlobalVars.beaconModelArrayListBeaconBoard.add(new BeaconPostModel(obj.getString("name"), Float.parseFloat(obj.get("rating").toString()) / 2, ((JSONObject)obj.get("location")).getString("formatted_address"), description, locationImages));
                                    Log.d(TAG, "beaconModelArrayList" + GlobalVars.beaconModelArrayListBeaconBoard.toString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, e.toString());
                            }

                            String beaconTitle = documentSnapshot.getString("title");
                            String beaconIntro = documentSnapshot.getString("intro");

                            Intent intent = new Intent(c, BeaconPost.class);
                            intent.putExtra("fromBeacons", true);
                            intent.putExtra("fromPassport", false);
                            intent.putExtra("adventourID", adventourID);
                            intent.putExtra("beaconTitle", beaconTitle);
                            intent.putExtra("beaconIntro", beaconIntro);
                            intent.putExtra("posterNickname", GlobalVars.beaconBoardArrayList.get(position).getBeaconAuthor());
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    public void getBeacons(String selectedBeaconLocation) {
        beaconsProgressBar.setVisibility(View.VISIBLE);

        Log.d("Inside getBeacons, loc: ", selectedBeaconLocation);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get all Beacons from Beacon Board.
        db.collection("Beacons")
                .whereEqualTo("beaconLocation", selectedBeaconLocation)
                .whereEqualTo("isPrivate", false)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        beaconsProgressBar.setVisibility(View.INVISIBLE);
                        Log.d("getBeacons in BEACONS", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         beaconsProgressBar.setVisibility(View.INVISIBLE);
                         if (task.isSuccessful())
                        {
                            Log.d("getBeacons in BEACONS", "Documents retrieved!");
                            for (QueryDocumentSnapshot beacons : task.getResult())
                            {
                                Map<String, Object> allData = new HashMap<>();
                                allData = beacons.getData();
                                allData.put("documentID", beacons.getId());

                                Log.d("Beacon Data", "All Beacon data: " + allData);
                                ArrayList<String> locations = (ArrayList<String>) allData.get("locations");
                                Log.d("getBeacons in BEACONS", locations.toString());

                                JSONObject requestBody = new JSONObject();

                                try
                                {
                                    requestBody.put("ids", new JSONArray(locations));
                                    requestBody.put("uid", user.getUid());
                                } catch (JSONException e) {
                                    Log.e("getBeacons in BEACONS Error", e.toString());
                                }

                                try {
                                    URL url = new URL("https://adventour-183a0.uc.r.appspot.com/get-foursquare-places");
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                                    conn.setDoOutput(true);
                                    conn.setInstanceFollowRedirects(false);
                                    conn.setRequestMethod("POST");
                                    conn.setRequestProperty("Content-Type", "application/json");
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setUseCaches(false);

                                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                                    wr.writeBytes(requestBody.toString());
                                    wr.flush();
                                    wr.close();
                                    requestBody = null;

                                    System.out.println("\nSending 'POST' request to URL : " + url);

                                    InputStream it = conn.getInputStream();
                                    InputStreamReader inputs = new InputStreamReader(it);

                                    BufferedReader in = new BufferedReader(inputs);
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }

                                    in.close();
                                    JSONObject responseData = new JSONObject(response.toString());
                                    JSONArray results = (JSONArray) responseData.get("results");
                                    allData.put("locations", results);
                                    setBeaconModel(allData);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("getBeaconPosts", e.toString());
                                }
                            }
                            Log.d("numOfUserBeacons", String.valueOf(GlobalVars.userBeaconsArrayList.size()));
                        }
                    }
                });
    }

    public void setBeaconModel(Map<String, Object> allData)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try {

            if (allData.get("uid") != null)
            {
                userId = (String) allData.get("uid");
            }

            // Get a reference to the user that posted
            DocumentReference documentRef = db.collection("Adventourists").document(userId);

            // Check if user document exists. If they do in this instance, get user info to populate passport.
            documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("User Id", document.getId());
                            Log.d("setBeaconModel in Beacons", "DocumentSnapshot data: " + document.getData());

                            if (document.get("nickname") != null)
                            {
                                nickname = (String) document.get("nickname");
                            } else {
                                nickname = "Adventourist";
                            }

                            if (AdventourUtils.isValidAndroidPfpRef(document))
                            {
                                androidPfpRef = toIntExact((long) document.get("androidPfpRef"));
                                Log.d("Android Pfp Ref ", String.valueOf(androidPfpRef));
                            } else if (AdventourUtils.isValidIOSPfpRef(document)) {
                                androidPfpRef = AdventourUtils.iOSToAndroidPfpRef((String) document.get("iosPfpRef"));
                                Log.d("ios Pfp Ref ", String.valueOf(androidPfpRef));
                            } else {
                                androidPfpRef = 6; // The case where a user does not have a profile pic on Android nor iOS.
                                Log.d("No Pfp Or Invalid Pfp Ref ", String.valueOf(androidPfpRef));
                            }

                            GlobalVars.beaconBoardArrayList.add(new BeaconsModel(allData, nickname, androidPfpRef));
                            beaconBoardAdapter.notifyDataSetChanged();

                            if (GlobalVars.beaconBoardArrayList.size() == 0) {
                                beaconsProgressBar.setVisibility(View.VISIBLE);
                            } else {
                                beaconsProgressBar.setVisibility(View.INVISIBLE);
                            }

                        } else {
                            Log.d("setBeaconModel in Beacons", "No such document");
                        }
                    } else {
                        Log.d("setBeaconModel in Beacons", "get failed with ", task.getException());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LocationImages getLocationImages(JSONArray photos) {
        LocationImages locationImages = new LocationImages();
        URL imageOneURL, imageTwoURL, imageThreeURL;
        HttpURLConnection connectionOne, connectionTwo, connectionThree;
        InputStream inputOne, inputTwo, inputThree;
        Bitmap bitmap;

        try {
            // Try to get first location image.
            if (photos.length() > 2) {
                String firstPrefix = photos.getJSONObject(0).get("prefix").toString();
                String firstSuffix = photos.getJSONObject(0).get("suffix").toString();

                String secondPrefix = photos.getJSONObject(1).get("prefix").toString();
                String secondSuffix = photos.getJSONObject(1).get("suffix").toString();

                String thirdPrefix = photos.getJSONObject(2).get("prefix").toString();
                String thirdSuffix = photos.getJSONObject(2).get("suffix").toString();

                imageOneURL = new URL(firstPrefix + "original" + firstSuffix);
                connectionOne = (HttpURLConnection) imageOneURL.openConnection();
                connectionOne.setDoInput(true);
                connectionOne.connect();
                inputOne = connectionOne.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputOne);
                locationImages.locationOne = bitmap;

                imageTwoURL = new URL(secondPrefix + "original" + secondSuffix);
                connectionTwo = (HttpURLConnection) imageTwoURL.openConnection();
                connectionTwo.setDoInput(true);
                connectionTwo.connect();
                inputTwo = connectionTwo.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputTwo);
                locationImages.locationTwo = bitmap;

                imageThreeURL = new URL(thirdPrefix + "original" + thirdSuffix);
                connectionThree = (HttpURLConnection) imageThreeURL.openConnection();
                connectionThree.setDoInput(true);
                connectionThree.connect();
                inputThree = connectionThree.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputThree);
                locationImages.locationThree = bitmap;

            } else if (photos.length() > 1) {
                String firstPrefix = photos.getJSONObject(0).get("prefix").toString();
                String firstSuffix = photos.getJSONObject(0).get("suffix").toString();

                String secondPrefix = photos.getJSONObject(1).get("prefix").toString();
                String secondSuffix = photos.getJSONObject(1).get("suffix").toString();

                imageOneURL = new URL(firstPrefix + "original" + firstSuffix);
                connectionOne = (HttpURLConnection) imageOneURL.openConnection();
                connectionOne.setDoInput(true);
                connectionOne.connect();
                inputOne = connectionOne.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputOne);
                locationImages.locationOne = bitmap;

                imageTwoURL = new URL(secondPrefix + "original" + secondSuffix);
                connectionTwo = (HttpURLConnection) imageTwoURL.openConnection();
                connectionTwo.setDoInput(true);
                connectionTwo.connect();
                inputTwo = connectionTwo.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputTwo);
                locationImages.locationTwo = bitmap;
            } else if (photos.length() > 0) {
                String firstPrefix = photos.getJSONObject(0).get("prefix").toString();
                String firstSuffix = photos.getJSONObject(0).get("suffix").toString();

                imageOneURL = new URL(firstPrefix + "original" + firstSuffix);
                connectionOne = (HttpURLConnection) imageOneURL.openConnection();
                connectionOne.setDoInput(true);
                connectionOne.connect();
                inputOne = connectionOne.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputOne);
                locationImages.locationOne = bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locationImages;
    }
}

