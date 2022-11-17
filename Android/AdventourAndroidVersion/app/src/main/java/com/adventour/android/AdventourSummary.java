package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AdventourSummary extends AppCompatActivity /*implements OnMapReadyCallback*/ {

    FirebaseAuth auth;
    FirebaseUser user;
    FloatingActionButton postBeaconButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventour_summary);

        postBeaconButton = (FloatingActionButton) findViewById(R.id.postBeaconButton);

        handleAuth();

        RecyclerView adventourSummaryRV = findViewById(R.id.adventourSummaryRV);
        adventourSummaryRV.setNestedScrollingEnabled(false);

        AdventourSummaryAdapter adventourSummaryAdapter;
        Bundle extras = getIntent().getExtras();
        if (extras != null && (boolean) extras.get("fromPassport")) {
            adventourSummaryAdapter = new AdventourSummaryAdapter(this, GlobalVars.adventourLocationsPassport);
        } else {
            adventourSummaryAdapter = new AdventourSummaryAdapter(this, GlobalVars.adventourLocations);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        adventourSummaryRV.setLayoutManager(linearLayoutManager);
        adventourSummaryRV.setAdapter(adventourSummaryAdapter);

        postBeaconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { switchToBeaconPost(); }
        });
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && (boolean) extras.get("fromPassport")) {
            Intent intent = new Intent(this, Passport.class);
            intent.putExtra("fromAdventourSummary", true);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, Congratulations.class);
            startActivity(intent);
            finish();
        }
    }


    public void switchToBeaconPost()
    {
        Intent intent = new Intent(this, BeaconPost.class);
        Bundle extras = getIntent().getExtras();
        if (extras != null && (boolean) extras.get("fromPassport")) {
            intent.putExtra("fromPassport", true);
            intent.putExtra("fromBeacons", false);
            intent.putExtra("adventourID", (String) extras.get("adventourID"));
        }

        startActivity(intent);
    }

    public void switchToLoggedOut()
    {
        Intent intent = new Intent(this, LoggedOut.class);
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
