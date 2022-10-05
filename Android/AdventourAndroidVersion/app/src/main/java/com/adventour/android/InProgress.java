package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class InProgress extends AppCompatActivity /*implements OnMapReadyCallback*/ {
    
    Context context;
    LinearLayout linearLayout;
    Button cardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.beacons);

        context = getApplicationContext();
        linearLayout = findViewById(R.id.linearLayout);
        cardButton = (Button) findViewById(R.id.cardButton);

        cardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newInProgressCard();
            }
        });

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

        /*
        // Google Maps API
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapsContainerView);
        mapFragment.getMapAsync(this);*/


    }

    /*
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {

    }

         */

    public void newInProgressCard()
    {
        FrameLayout.LayoutParams layoutParams;
        FrameLayout.LayoutParams showOnMapParams;
        FrameLayout.LayoutParams locationParams;
        FrameLayout.LayoutParams addPhotoParams;
        FrameLayout frameLayout;
        ImageView imageView;
        TextView showOnMap;
        TextView locations;
        TextView addPhoto;

        imageView = new ImageView(context);
        showOnMap = new TextView(context);
        locations = new TextView(context);
        frameLayout = new FrameLayout(context);
        addPhoto = new TextView(context);
        layoutParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        showOnMapParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        locationParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        addPhotoParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        showOnMap.setPadding(0, 0, 0, 0);

        locationParams.setMargins(90, 90, 0, 0);
        showOnMapParams.setMargins(90, 150, 0, 0);
        addPhotoParams.setMargins(90, 205, 0, 30);

        locations.setLayoutParams(locationParams);
        locations.setText("University of Central Florida");
        locations.setTextSize(18);
        locations.setTextColor(getResources().getColor(R.color.black));

        showOnMap.setLayoutParams(showOnMapParams);
        showOnMap.setText("Show on Map");
        showOnMap.setTextSize(16);
        showOnMap.setTextColor(getResources().getColor(R.color.red_variant));
        showOnMap.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        showOnMap.setClickable(true);

        addPhoto.setLayoutParams(addPhotoParams);
        addPhoto.setText("Add Photo");
        addPhoto.setTextSize(16);
        addPhoto.setTextColor(getResources().getColor(R.color.blue_main));
        addPhoto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        addPhoto.setClickable(true);
        addPhoto.setFocusable(true);

        frameLayout.setLayoutParams(layoutParams);
        frameLayout.addView(imageView);
        frameLayout.addView(showOnMap);
        frameLayout.addView(locations);
        frameLayout.addView(addPhoto);
        linearLayout.addView(frameLayout);
    }
}
