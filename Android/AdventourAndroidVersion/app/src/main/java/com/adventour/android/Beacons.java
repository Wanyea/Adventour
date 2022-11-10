package com.adventour.android;

import static java.lang.Math.toIntExact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Beacons extends AppCompatActivity {


    BeaconsAdapter beaconBoardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);

        GlobalVars.previousAdventourArrayList.clear();
        GlobalVars.userBeaconsArrayList.clear();

        handleAuth();
        getBeacons();

        RecyclerView beaconsRV = findViewById(R.id.beaconsRV);
        beaconsRV.setNestedScrollingEnabled(false);

        beaconBoardAdapter = new BeaconsAdapter(this, GlobalVars.beaconBoardArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        beaconsRV.setLayoutManager(linearLayoutManager);
        beaconsRV.setAdapter(beaconBoardAdapter);


        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.beacons);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnItemSelectedListener) item -> {

            switch (item.getItemId()) {
                case R.id.passport:
                    startActivity(new Intent(getApplicationContext(), Passport.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.start_adventour:
                    startActivity(new Intent(getApplicationContext(), StartAdventour.class));
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

    public void switchToBeaconPost() {
        Intent intent = new Intent(this, BeaconPost.class);
        startActivity(intent);
        finish();
    }

    public void getBeacons() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get all Beacons from Beacon Board.
        Log.d("getBeacons in BEACONS", "Calling database...");
        db.collection("Beacons")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("getBeacons in BEACONS", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("getBeacons in BEACONS", "Completed calling database...");
                        if (task.isSuccessful())
                        {
                            Log.d("getBeacons in BEACONS", "Documents retrieved!");
                            for (QueryDocumentSnapshot beacons : task.getResult())
                            {
                                Map<String, Object> allData = new HashMap<>();
                                allData = beacons.getData();
                                allData.put("documentID", beacons.getId());

                                Log.d("BEACON DATA", "all beacon data: " + allData);
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

                                    beaconBoardAdapter.notifyDataSetChanged();
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
            Map<String, Object> user = (Map<String, Object>) allData.get("author");
            String userId = (String) user.get("uid");

            // Get a reference to the user that posted
            DocumentReference documentRef = db.collection("Adventourists").document(userId);
            // Check if user document exists. If they do in this instance, populate passport wth user data.
            documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("BEACONS", "DocumentSnapshot data: " + document.getData());
                            String nickname = (String) document.get("nickname");

                            int androidPfpRef = toIntExact((long) document.get("androidPfpRef"));
                            GlobalVars.beaconBoardArrayList.add(new BeaconsModel(allData, nickname, androidPfpRef));

                        } else {
                            Log.d("BEACONS", "No such document");
                        }
                    } else {
                        Log.d("BEACONS", "get failed with ", task.getException());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

