package com.adventour.android;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.Window;
import android.widget.Button;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InProgress extends AppCompatActivity implements OnMapReadyCallback {

    Context context = this;
    OnMapReadyCallback callback;

    FirebaseAuth auth;
    FirebaseUser user;

    Button finishAdventourButton;
    Double lat = GlobalVars.inProgressModelArrayList.get(0).getLatitude();
    Double lon = GlobalVars.inProgressModelArrayList.get(0).getLongitude();
    String locationName = GlobalVars.inProgressModelArrayList.get(0).getName();
    String locationAddress = GlobalVars.beaconModelArrayList.get(0).getAddress();

    FloatingActionButton addLocationButton;

    HashMap<String, String> isSwitchActive = new HashMap<>();
    int distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);

        handleAuth();

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

        // Google map code
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        assert mapFragment != null;
        callback = this;

        RecyclerView InProgressRV = findViewById(R.id.inProgressRV);

        InProgressRV.setNestedScrollingEnabled(false);
        InProgressRV.addOnItemTouchListener(
                new InProgressClickListener(this, InProgressRV, new InProgressClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        lat = GlobalVars.inProgressModelArrayList.get(position).getLatitude();
                        lon = GlobalVars.inProgressModelArrayList.get(position).getLongitude();
                        locationName = GlobalVars.inProgressModelArrayList.get(position).getName();
                        locationAddress = GlobalVars.beaconModelArrayList.get(position).getAddress();
                        mapFragment.getMapAsync(callback);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) { }
                })
        );


        InProgressRV.setNestedScrollingEnabled(true);

        addLocationButton = (FloatingActionButton) findViewById(R.id.addLocationButton);
        finishAdventourButton = (Button) findViewById(R.id.finishAdventourButton);

        InProgressAdapter inProgressAdapter = new InProgressAdapter(this, GlobalVars.inProgressModelArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        InProgressRV.setLayoutManager(linearLayoutManager);
        InProgressRV.setAdapter(inProgressAdapter);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.beacons);

        context = getApplicationContext();

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnItemSelectedListener) item -> {

            switch(item.getItemId())
            {
                case R.id.passport:
                    startActivity(new Intent(getApplicationContext(), Passport.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.start_adventour:
                    return true;
                case R.id.beacons:
                    startActivity(new Intent(getApplicationContext(), Beacons.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        finishAdventourButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
//                storeAdventour();
                switchToCongratulations();
           }
        });

        addLocationButton.setOnClickListener(new  View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
                switchToStartAdventour();
           }
        });
    }

    @Override
    public void onBackPressed() {
        switchToStartAdventour();
    }

    public void switchToLoggedOut()
    {
        Intent intent = new Intent(this, LoggedOut.class);
        startActivity(intent);
        finish();
    }

    public void switchToStartAdventour()
    {
        Intent intent = new Intent(this, StartAdventour.class);
        intent.putExtra("isSwitchActive", isSwitchActive);
        intent.putExtra("distance", distance);
        startActivity(intent);
    }

    public void switchToCongratulations()
    {
        Intent intent = new Intent(this, Congratulations.class);
        startActivity(intent);
        finish();
    }

    public void handleAuth()
    {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null)
        {
            switchToLoggedOut();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        map.clear();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 13));
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon));
        Marker mapMarker = map.addMarker(marker);
        mapMarker.setSnippet("Tap for directions");
        mapMarker.setTitle(locationName);
        mapMarker.showInfoWindow();

        // Open in maps code
        map.setOnMarkerClickListener(marker1 -> {
            Uri gmmIntentUri = Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "?z=17&q=" + Uri.encode(locationName + " " + locationAddress));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
            return true;
        });
        map.setOnInfoWindowClickListener(marker1 -> {
            Uri gmmIntentUri = Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "?z=17&q=" + Uri.encode(locationName + " " + locationAddress));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
    }

    public void storeAdventour()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> newAdventour= new HashMap<>();

        newAdventour.put("dateCreated", new Timestamp(new Date()));
        newAdventour.put("locations", GlobalVars.adventourFSQIds);
        newAdventour.put("numLocations", GlobalVars.adventourFSQIds.size());
        newAdventour.put("isBeacon", false);

        //TODO: for future versions of the app: it would be nice to store categories here so they can be displayed on the prevAdventour/beacon cards
        // and users could potentially filter by categories.

        db.collection("Adventourists")
                .document(user.getUid())
                .collection("adventours")
                .add(newAdventour)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("New Adventour added", "DocumentSnapshot written with ID: " + documentReference.getId());
//                        storeBeacon(documentReference.getId()); // Why this called?
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed to add Adventour", "Error adding document", e);
                    }
                });
    }

    public void storeBeacon(String adventourId)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> addBeacon = new HashMap<>();
        addBeacon.put("dateCreated", new Timestamp(new Date()));
        addBeacon.put("dateUpdated", new Timestamp(new Date()));
        addBeacon.put("locations", GlobalVars.adventourFSQIds);
        addBeacon.put("numLocations", GlobalVars.adventourFSQIds.size());
        addBeacon.put("uid", user.getUid());
        addBeacon.put("title", "Beacon Title");
        addBeacon.put("intro", "This is where you can give your Beacon a meaningful description!");
        addBeacon.put("isPrivate", true);
        addBeacon.put("locationDescriptions", GlobalVars.locationDescriptions);
        addBeacon.put("beaconLocation", GlobalVars.selectedLocation);

        db.collection("Adventourists")
                .document(user.getUid())
                .collection("beacons")
                .document(adventourId)
                .set(addBeacon)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void v) {
                        Log.d("Beacon added", "DocumentSnapshot written with ID: " + v);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed to add beacon", "Error adding document", e);
                    }
                });
    }
}
