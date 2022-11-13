package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Congratulations extends AppCompatActivity {

    Button postBeaconButton, viewAdventourButton, homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulations);

        postBeaconButton = (Button) findViewById(R.id.goToBeaconPostButton);
        viewAdventourButton = (Button) findViewById(R.id.viewAdventourButton);
        homeButton = (Button) findViewById(R.id.homeButton);


        postBeaconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { switchToBeaconPost(); }
        });

        viewAdventourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToAdventourSummary();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Get Start Adventour Activity for next Adventour.
                GlobalVars.adventourLocations.clear();
                GlobalVars.adventourFSQIds.clear();
                GlobalVars.excludes = new JSONArray();
                GlobalVars.inProgressModelArrayList.clear();
                GlobalVars.beaconModelArrayList.clear();
                GlobalVars.locationDescriptions.clear();
                GlobalVars.previousAdventourArrayList.clear();
                GlobalVars.userBeaconsArrayList.clear();

                switchToStartAdventour();
            }
        });
    }

    public void switchToBeaconPost()
    {
        Intent intent = new Intent(this, BeaconPost.class);
        startActivity(intent);
    }

    // Testing only, delete before prod
    public void switchToAdventourSummary()
     {
        Intent intent = new Intent(this, AdventourSummary.class);
        startActivity(intent);
    }

    public void switchToStartAdventour()
    {
        Intent intent = new Intent(this, StartAdventour.class);
        startActivity(intent);
        finish();
    }



}