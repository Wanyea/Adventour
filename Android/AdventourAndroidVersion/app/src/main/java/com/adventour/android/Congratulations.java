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
            public void onClick(View view) {

            }
        });

        viewAdventourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToStartAdventour();
            }
        });
    }

    public void switchToBeaconPost()
    {
        // Put intent here once Bug has finished this page
        // startActivity(intent);
        // finish();
    }

    public void switchToAdventourSummary()
    {
        // Put intent here once Ryan has finished this page
        // startActivity(intent);
        // finish();
    }

    public void switchToStartAdventour()
    {
        Intent intent = new Intent(this, StartAdventour.class);
        startActivity(intent);
        finish();
    }
}