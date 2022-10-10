package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AdventourSummary extends AppCompatActivity /*implements OnMapReadyCallback*/ {

    Context context;
    LinearLayout linearLayout;
    Button cardButton;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventour_summary);

        handleAuth();

        RecyclerView adventourSummaryRV = findViewById(R.id.adventourSummaryRV);
        adventourSummaryRV.setNestedScrollingEnabled(false);

        // TEST DATA - WILL BE REPLACED BY DATA RETURN FROM API.
        ArrayList<AdventourSummaryModel> adventourArrayList = new ArrayList<AdventourSummaryModel>();
        adventourArrayList.add(new AdventourSummaryModel("UCF", "University of Central Florida"));
        adventourArrayList.add(new AdventourSummaryModel("UCF", "University of Central Florida"));
        adventourArrayList.add(new AdventourSummaryModel("UCF", "University of Central Florida"));
        adventourArrayList.add(new AdventourSummaryModel("UCF", "University of Central Florida"));
        adventourArrayList.add(new AdventourSummaryModel("UCF", "University of Central Florida"));

        AdventourSummaryAdapter adventourSummaryAdapter = new AdventourSummaryAdapter(this, adventourArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        adventourSummaryRV.setLayoutManager(linearLayoutManager);
        adventourSummaryRV.setAdapter(adventourSummaryAdapter);


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
}
