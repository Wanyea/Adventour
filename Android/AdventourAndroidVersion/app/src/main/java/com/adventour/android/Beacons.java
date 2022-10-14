package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Beacons extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);

        handleAuth();

//        button = findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switchToBeaconPost();
//            }
//        });


        RecyclerView beaconsRV = findViewById(R.id.beaconsRV);
        beaconsRV.setNestedScrollingEnabled(false);

        // TEST DATA - WILL BE REPLACED BY DATA RETURN FROM API.
        ArrayList<BeaconsModel> beaconsArrayList = new ArrayList<BeaconsModel>();
        beaconsArrayList.add(new BeaconsModel("\u25CFUniversity of Central Florida\n\u25CFThe Cloak and Blaster\n\u25CF American Escape Rooms Orlando", "01/01/2001", "imageURL"));
        beaconsArrayList.add(new BeaconsModel("\u25CFUniversity of Central Florida\n\u25CFThe Cloak and Blaster\n\u25CF American Escape Rooms Orlando", "01/01/2001", "imageURL"));
        beaconsArrayList.add(new BeaconsModel("\u25CFUniversity of Central Florida\n\u25CFThe Cloak and Blaster\n\u25CF American Escape Rooms Orlando", "01/01/2001", "imageURL"));
        beaconsArrayList.add(new BeaconsModel("\u25CFUniversity of Central Florida\n\u25CFThe Cloak and Blaster\n\u25CF American Escape Rooms Orlando", "01/01/2001", "imageURL"));
        beaconsArrayList.add(new BeaconsModel("\u25CFUniversity of Central Florida\n\u25CFThe Cloak and Blaster\n\u25CF American Escape Rooms Orlando", "01/01/2001", "imageURL"));

        BeaconsAdapter beaconsAdapter = new BeaconsAdapter(this, beaconsArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        beaconsRV.setLayoutManager(linearLayoutManager);
        beaconsRV.setAdapter(beaconsAdapter);


        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.beacons);

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
    }

    public void switchToLoggedOut()
    {
        Intent intent = new Intent(this, MainActivity.class);
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

    public void switchToBeaconPost()
    {
        Intent intent = new Intent(this, BeaconPost.class);
        startActivity(intent);
        finish();
    }
}

