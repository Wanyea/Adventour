package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    Double lat = GlobalVars.inProgressModelArrayList.get(0).getLatitude();
    Double lon = GlobalVars.inProgressModelArrayList.get(0).getLongitude();
    String locationName = GlobalVars.inProgressModelArrayList.get(0).getName();
    String locationAddress = GlobalVars.beaconModelArrayList.get(0).getAddress();

    FloatingActionButton addLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);
        handleAuth();

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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 13));
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon));
        Marker mapMarker = map.addMarker(marker);
        mapMarker.setSnippet("Tap pin for directions");
        mapMarker.setTitle(locationName);
        mapMarker.showInfoWindow();

        // Open in maps code
        map.setOnMarkerClickListener(marker1 -> {
//            Uri gmmIntentUri = Uri.parse("geo:0,0?z=18&q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
            Uri gmmIntentUri = Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "?z=17&q=" + Uri.encode(locationName + " " + locationAddress));
//            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
            return true;
        });
    }
}
