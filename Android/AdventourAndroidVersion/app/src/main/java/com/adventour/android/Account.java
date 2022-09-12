package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Account extends AppCompatActivity {
    CardView cardView, cardView2;
    ImageView arrow, arrow2;
    Group hiddenGroup, hiddenGroup2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        cardView = findViewById(R.id.passportCardView);
        arrow = findViewById(R.id.show);
        hiddenGroup = findViewById(R.id.card_group);

        arrow.setOnClickListener(view -> {
            if(hiddenGroup.getVisibility() == View.VISIBLE){
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                hiddenGroup.setVisibility(View.GONE);
                arrow.setImageResource(android.R.drawable.arrow_down_float);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                hiddenGroup.setVisibility(View.VISIBLE);
                arrow.setImageResource(android.R.drawable.arrow_up_float);
            }
        });

        cardView2 = findViewById(R.id.beaconsCardView);
        arrow2 = findViewById(R.id.show2);
        hiddenGroup2 = findViewById(R.id.card_group_2);

        arrow2.setOnClickListener(view -> {
            if(hiddenGroup2.getVisibility() == View.VISIBLE){
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                hiddenGroup2.setVisibility(View.GONE);
                arrow2.setImageResource(android.R.drawable.arrow_down_float);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                hiddenGroup2.setVisibility(View.VISIBLE);
                arrow2.setImageResource(android.R.drawable.arrow_up_float);
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.account);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnItemSelectedListener) item -> {

            switch(item.getItemId())
            {
                case R.id.account:
                    return true;
                case R.id.start_adventour:
                    startActivity(new Intent(getApplicationContext(), StartAdventour.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.beacons:
                    startActivity(new Intent(getApplicationContext(), Beacons.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }
}