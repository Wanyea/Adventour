package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.LinearLayout;


import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Passport extends AppCompatActivity {
    
    private static final String TAG = "PassportActivity";

    ImageButton imageButton;

    TextView nicknameTextView, birthdateTextView, mantraTextView;

    FirebaseAuth auth;
    FirebaseUser user;


    Context context;
    LinearLayout linearLayout, linearLayout2;

    Menu hamburgerMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);


        context = getApplicationContext();
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout2 = findViewById(R.id.linearLayout2);


        imageButton = (ImageButton) findViewById(R.id.imageButton);

        nicknameTextView = (TextView) findViewById(R.id.nicknameTextView);
        birthdateTextView = (TextView) findViewById(R.id.birthdateTextView);
        mantraTextView = (TextView) findViewById(R.id.mantraTextView);

        handleAuth();
        populatePassportTextViews();
        populatePreviousAdventours();



        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchToPassportMoreInfo();
            }
        });



        for (int i = 0; i < 3; i++) {
            newPreviousAdventourCard();
        }

        for (int i = 0; i < 3; i++) {
            newLitBeaconCard();
        }

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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hamburger_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Build AlertDialog that will alert users when they try to log out.
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Are you sure you want to log out?");
        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        FirebaseAuth.getInstance().signOut();
                        switchToLoggedOut();
                    }
                });

        alertBuilder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        switch(item.getItemId())
        {
            case R.id.logOut:
                AlertDialog alert = alertBuilder.create();
                alert.show();
                return true;

            case R.id.settings:
                switchToSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void newPreviousAdventourCard() {
        FrameLayout.LayoutParams layoutParams;
        FrameLayout.LayoutParams dateParams;
        FrameLayout.LayoutParams locationParams;
        FrameLayout.LayoutParams moreInfoParams;
        FrameLayout frameLayout;
        ImageView background;
        TextView dates;
        TextView locations;
        TextView moreInfo;

        background = new ImageView(context);
        dates = new TextView(context);
        locations = new TextView(context);
        frameLayout = new FrameLayout(context);
        moreInfo = new TextView(context);

        layoutParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        dateParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        locationParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        moreInfoParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        background.setPadding(0, 0, 0, 40);
        background.setImageResource(R.drawable.ic_stamp_card);

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
        frameLayout.addView(background);
        frameLayout.addView(dates);
        frameLayout.addView(locations);
        frameLayout.addView(moreInfo);

        linearLayout.addView(frameLayout);
    }

    public void newLitBeaconCard() {
        FrameLayout.LayoutParams layoutParams;
        FrameLayout.LayoutParams imageViewParams;
        FrameLayout.LayoutParams dateParams;
        FrameLayout.LayoutParams locationParams;
        FrameLayout.LayoutParams backgroundParams;
        FrameLayout.LayoutParams moreInfoParams;

        layoutParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        dateParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        locationParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        imageViewParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        backgroundParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        moreInfoParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        FrameLayout frameLayout;
        ImageView background;
        ImageView imageView;
        TextView date;
        TextView locations;
        TextView moreInfo;

        frameLayout = new FrameLayout(context);
        background = new ImageView(context);
        imageView = new ImageView(context);
        date = new TextView(context);
        locations = new TextView(context);
        moreInfo = new TextView(context);

        backgroundParams.setMargins(0, 0, 0, 0);
        backgroundParams.height = 750;

        background.setPadding(0, 0, 0, 40);
        background.setImageResource(R.drawable.ic_beacon_card);
        background.setLayoutParams(backgroundParams);

        imageViewParams.setMargins(110, 170, 0, 0);
        imageViewParams.height = 350;
        imageViewParams.width = 350;

        imageView.setLayoutParams(imageViewParams);
        imageView.setImageResource(R.drawable.ic_map_scroll);

        dateParams.setMargins(750, 25, 0, 0);

        date.setLayoutParams(dateParams);
        date.setText("01/01/1000");
        date.setTextColor(getResources().getColor(R.color.navy_main));
        date.setTextSize(16);
        date.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

        locationParams.setMargins(550, 120, 0, 0);
        locationParams.height = 500;
        locationParams.width = 400;

        locations.setLayoutParams(locationParams);
        locations.setText("\u25CFUniversity of Central Florida\n\u25CFThe Cloak and Blaster\n\u25CF American Escape Rooms Orlando");
        locations.setTextColor(getResources().getColor(R.color.navy_main));
        locations.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        locations.setTextSize(16);

        moreInfoParams.setMargins(800, 630, 0, 0);

        moreInfo.setLayoutParams(moreInfoParams);
        moreInfo.setText("> More...");
        moreInfo.setTextColor(getResources().getColor(R.color.blue_main));
        moreInfo.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        moreInfo.setClickable(true);
        moreInfo.setFocusable(true);

        frameLayout.setLayoutParams(layoutParams);
        frameLayout.addView(background);
        frameLayout.addView(imageView);
        frameLayout.addView(date);
        frameLayout.addView(locations);
        frameLayout.addView(moreInfo);

        linearLayout2.addView(frameLayout);
    }

    public void populatePassportTextViews()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the user
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());
        // Check if user document exists. If they do in this instance, populate passport wth user data.
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        nicknameTextView.setText(document.getString("nickname"));
                        birthdateTextView.setText(document.getString("birthdate"));
                        mantraTextView.setText(document.getString("mantra"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void populatePreviousAdventours()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the user
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());
        documentRef.collection("adventours")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot adventour : task.getResult()) {
                                Log.d(TAG, adventour.getId() + " => " + adventour.getData());
                                db.collection("Adventourists")
                                        .document(user.getUid())
                                        .collection("adventours")
                                        .document(adventour.getId())
                                        .collection("locations")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    for(QueryDocumentSnapshot location : task.getResult()) {

                                                        Log.d(TAG, "locations: " + location.getData());
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }

                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void switchToPassportMoreInfo()
    {
        Intent intent = new Intent(this, EditPassport.class);
        startActivity(intent);
        finish();
    }

    public void switchToSettings()
    {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
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