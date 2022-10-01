package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.io.Resources;

import org.w3c.dom.Text;

public class Passport extends AppCompatActivity {

    ImageButton imageButton;
    ImageButton hamburgerMenu;

    Context context;
    Button button;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);

        context = getApplicationContext();
        button = findViewById(R.id.addCard);
        linearLayout = findViewById(R.id.linearLayout);


        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PassportMoreInfo.class);
                startActivity(intent);
            }
        });

        hamburgerMenu = (ImageButton) findViewById(R.id.hamburger_menu);
        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Settings.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newPreviousAdventourCard();
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.passport);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnItemSelectedListener) item -> {

            switch (item.getItemId()) {
                case R.id.passport:
                    return true;
                case R.id.start_adventour:
                    startActivity(new Intent(getApplicationContext(), StartAdventour.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.beacons:
                    startActivity(new Intent(getApplicationContext(), Beacons.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    public void newPreviousAdventourCard() {
        FrameLayout.LayoutParams layoutParams;
        FrameLayout.LayoutParams dateParams;
        FrameLayout.LayoutParams locationParams;
        FrameLayout.LayoutParams moreInfoParams;
        FrameLayout frameLayout;
        ImageView imageView;
        TextView dates;
        TextView locations;
        TextView moreInfo;

        imageView = new ImageView(context);
        dates = new TextView(context);
        locations = new TextView(context);
        frameLayout = new FrameLayout(context);
        moreInfo = new TextView(context);
        layoutParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        dateParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        locationParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        moreInfoParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        imageView.setPadding(0, 0, 0, 40);
        imageView.setImageResource(R.drawable.ic_stamp_card);

        dateParams.setMargins(30, 25, 0, 0);

        dates.setLayoutParams(dateParams);
        dates.setText("3/18/2000 - 3/19/2000");
        dates.setTextSize(18);
        dates.setTextColor(getResources().getColor(R.color.red_variant));
        dates.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        locationParams.setMargins(70, 150, 0, 0);

        locations.setLayoutParams(locationParams);
        locations.setText("\u25CF University of Central Florida\n\u25CF The Cloak and Blaster\n\u25CF American Escape Rooms Orlando");
        locations.setTextSize(16);
        locations.setTextColor(getResources().getColor(R.color.red_variant));
        locations.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        moreInfoParams.setMargins(30, 420, 0, 0);

        moreInfo.setLayoutParams(moreInfoParams);
        moreInfo.setText("> More...");
        moreInfo.setTextSize(16);
        moreInfo.setTextColor(getResources().getColor(R.color.red_variant));
        moreInfo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        moreInfo.setClickable(true);
        moreInfo.setFocusable(true);

        frameLayout.setLayoutParams(layoutParams);
        frameLayout.addView(imageView);
        frameLayout.addView(dates);
        frameLayout.addView(locations);
        frameLayout.addView(moreInfo);
        linearLayout.addView(frameLayout);
    }
}