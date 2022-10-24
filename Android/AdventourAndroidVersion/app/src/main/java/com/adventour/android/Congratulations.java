package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Congratulations extends AppCompatActivity {

    Button postBeaconButton, viewAdventourButton, homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulations);

        postBeaconButton = (Button) findViewById(R.id.postBeaconButton);
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
                GlobalVars.exclude.clear();
                GlobalVars.inProgressModelArrayList.clear();
                GlobalVars.beaconModelArrayList.clear();
                switchToStartAdventour();
            }
        });
    }

    public void switchToBeaconPost()
    {
        Intent intent = new Intent(this, BeaconPost.class);
        startActivity(intent);
        finish();
    }

    // Testing only, delete before prod
    public void switchToAdventourSummary()
     {
        Intent intent = new Intent(this, AdventourSummary.class);
        startActivity(intent);
        finish();
    }

    public void switchToStartAdventour()
    {
        Intent intent = new Intent(this, StartAdventour.class);
        startActivity(intent);
        finish();
    }


}