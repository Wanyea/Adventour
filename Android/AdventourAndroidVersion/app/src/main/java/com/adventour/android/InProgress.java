package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class InProgress extends AppCompatActivity /*implements OnMapReadyCallback*/ {
    
    Context context;

    FirebaseAuth auth;
    FirebaseUser user;

    FloatingActionButton finishAdventourButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);


        handleAuth();

        RecyclerView InProgressRV = findViewById(R.id.inProgressRV);
        InProgressRV.setNestedScrollingEnabled(false);

        finishAdventourButton = (FloatingActionButton) findViewById(R.id.finishAdventourButton);

        // TEST DATA - WILL BE REPLACED BY DATA RETURN FROM API.

        ArrayList<InProgressModel> inProgressModelArrayList = new ArrayList<InProgressModel>();
        inProgressModelArrayList.add(new InProgressModel("University of Central Florida", String.valueOf(Math.random()), String.valueOf(Math.random())));
        inProgressModelArrayList.add(new InProgressModel("The Cloak & Blaster", String.valueOf(Math.random()), String.valueOf(Math.random())));
        inProgressModelArrayList.add(new InProgressModel("American Escape Rooms Orlando", String.valueOf(Math.random()), String.valueOf(Math.random())));
        inProgressModelArrayList.add(new InProgressModel("Arcade Monsters", String.valueOf(Math.random()), String.valueOf(Math.random())));
        inProgressModelArrayList.add(new InProgressModel("Congo River Golf", String.valueOf(Math.random()), String.valueOf(Math.random())));

        InProgressAdapter inProgressAdapter = new InProgressAdapter(this, inProgressModelArrayList);

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

        finishAdventourButton.setOnClickListener(new  View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
                switchToCongratulations();
           }
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

    public void switchToLoggedOut()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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
}
