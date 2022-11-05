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
                storeAdventour();

                // Get Start Adventour Activity for next Adventour.
                GlobalVars.adventourLocations.clear();
                GlobalVars.adventourFSQIds.clear();
                GlobalVars.excludes.clear();
                GlobalVars.inProgressModelArrayList.clear();
                GlobalVars.beaconModelArrayList.clear();
                GlobalVars.locationDescriptions.clear();
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

    public void storeAdventour()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Calendar today = Calendar.getInstance();
        Map<String, Object> newAdventour= new HashMap<>();

        // Get a reference to the user
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());

        // Check if user document exists. If they do in this instance, attach users nickname and profile pic.

        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            private static final String TAG = "CONGRATULATIONS";

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        newAdventour.put("nickname", document.getString("nickname"));
                        // TODO: get reference to users profile pic.
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        newAdventour.put("dateCreated", new Timestamp(new Date()));
        newAdventour.put("locations", GlobalVars.adventourFSQIds);
        newAdventour.put("numLocations", GlobalVars.adventourFSQIds.size());

        //TODO: for future versions of the app: it would be nice to store categories here so they can be displayed on the prevAdventour/beacon cards
        // and users could potentially filter by categories.

        //TODO: fields for beacon title and intro???

        db.collection("Adventourists")
                .document(user.getUid())
                .collection("adventours")
                .add(newAdventour)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("New Adventour added", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed to add Adventour", "Error adding document", e);
                    }
                });
    }

}