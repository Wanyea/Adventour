package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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

import java.util.ArrayList;
import java.util.Collection;

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
        getBeaconPosts();

 /*       RecyclerView beaconsRV = findViewById(R.id.beaconsRV);
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
        beaconsRV.setAdapter(beaconsAdapter);*/


        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.beacons);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnItemSelectedListener) item -> {

            switch (item.getItemId()) {
                case R.id.passport:
                    startActivity(new Intent(getApplicationContext(), Passport.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.start_adventour:
                    startActivity(new Intent(getApplicationContext(), StartAdventour.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.beacons:
                    return true;
            }
            return false;
        });
    }

    public void switchToLoggedOut() {
        Intent intent = new Intent(this, LoggedOut.class);
        startActivity(intent);
        finish();
    }

    public void handleAuth() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            switchToLoggedOut();
        }
    }

    public void switchToBeaconPost() {
        Intent intent = new Intent(this, BeaconPost.class);
        startActivity(intent);
        finish();
    }

    public void getBeaconPosts() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<BeaconPostModel> previousAdventours = new ArrayList<>();

        // Get all Beacons from Beacon Board.
        db.collection("Beacons")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "Beacon Board";

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot beacon : task.getResult()) {
                                Log.d("ALL BEACONS", beacon.get("locations").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}

