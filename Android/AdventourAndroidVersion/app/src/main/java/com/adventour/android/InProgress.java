package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.view.Window;
import android.widget.Button;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class InProgress extends AppCompatActivity implements OnMapReadyCallback {

    Context context;
    OnMapReadyCallback callback;

    FirebaseAuth auth;
    FirebaseUser user;

    FloatingActionButton finishAdventourButton;
    Double lat = 0.0;
    Double lon = 0.0;
    String locationName = "Null Island";

    FloatingActionButton addLocationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);

        // Google map code
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(this);
        assert mapFragment != null;
        callback = this;
        
        handleAuth();

        RecyclerView InProgressRV = findViewById(R.id.inProgressRV);

        InProgressRV.setNestedScrollingEnabled(false);
        InProgressRV.addOnItemTouchListener(
                new InProgressClickListener(this, InProgressRV, new InProgressClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        lat = 28.602427; // replace with lat from whatever api returns
                        lon = -81.200058; // same but lon
                        locationName = "UCF"; // same but name
                        mapFragment.getMapAsync(callback);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) { }
                })
        );


        InProgressRV.setNestedScrollingEnabled(true);

        addLocationButton = (FloatingActionButton) findViewById(R.id.addLocationButton);

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
                    startActivity(new Intent(getApplicationContext(), StartAdventour.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.beacons:
                    return true;
            }
            return false;
        });

        addLocationButton.setOnClickListener(new  View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
                switchToStartAdventour();
           }
        });
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
        map.addMarker(new MarkerOptions().position(new LatLng( lat, lon)).title(locationName));
    }
}
