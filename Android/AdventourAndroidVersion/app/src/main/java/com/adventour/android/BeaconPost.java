package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BeaconPost extends AppCompatActivity {

    TextView beaconPostDate;
    ImageButton postBeaconButton;
    EditText beaconTitleEditText, beaconIntroEditText;
    boolean isEditMode = true;
    String titleString, introString;
    Switch isPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_post);

        beaconPostDate = (TextView) findViewById(R.id.beaconPostDate);
        postBeaconButton = (ImageButton) findViewById(R.id.postBeaconButton);
        beaconTitleEditText = (EditText) findViewById(R.id.beaconTitleEditText);
        beaconIntroEditText = (EditText) findViewById(R.id.beaconIntroEditText);

        isPrivate = (Switch) findViewById(R.id.privateSwitch);

        beaconPostDate.setText(AdventourUtils.formatBirthdateFromDatabase(new Timestamp(new Date())));

        RecyclerView beaconPostRV = findViewById(R.id.beaconPostRV);
        beaconPostRV.setNestedScrollingEnabled(true);

        BeaconPostAdapter BeaconPostAdapter = new BeaconPostAdapter(this, GlobalVars.beaconModelArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        beaconPostRV.setLayoutManager(linearLayoutManager);
        beaconPostRV.setAdapter(BeaconPostAdapter);


        postBeaconButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               postToBeaconBoard();
               storeBeacon();
               switchToHome();
           }
        });
    }

    public void postToBeaconBoard()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> newBeacon = new HashMap<>();
        newBeacon.put("dateCreated", new Timestamp(new Date()));
        newBeacon.put("dateUpdated", new Timestamp(new Date()));
        newBeacon.put("locations", GlobalVars.adventourFSQIds);
        newBeacon.put("numLocations", GlobalVars.adventourFSQIds.size());
        newBeacon.put("uid", user.getUid());
        newBeacon.put("title", beaconTitleEditText.getText().toString());
        newBeacon.put("intro", beaconIntroEditText.getText().toString());
        newBeacon.put("isPrivate", isPrivate.isChecked());
        newBeacon.put("locationDescriptions", GlobalVars.locationDescriptions);
        newBeacon.put("beaconLocation", );
        db.collection("Adventourists")
                .document(user.getUid())
                .collection("Beacons")
                .add(newBeacon)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Beacon Posted", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed to post beacon", "Error adding document", e);
                    }
                });
    }

    public void storeBeacon()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> addBeacon = new HashMap<>();
        addBeacon.put("dateCreated", new Timestamp(new Date()));
        addBeacon.put("dateUpdated", new Timestamp(new Date()));
        addBeacon.put("locations", GlobalVars.adventourFSQIds);
        addBeacon.put("numLocations", GlobalVars.adventourFSQIds.size());
        addBeacon.put("uid", user.getUid());
        addBeacon.put("title", beaconTitleEditText.getText().toString());
        addBeacon.put("intro", beaconIntroEditText.getText().toString());
        addBeacon.put("isPrivate", isPrivate.isChecked());
        addBeacon.put("locationDescriptions", GlobalVars.locationDescriptions);

        db.collection("Adventourists")
                .document(user.getUid())
                .collection("beacons")
                .add(addBeacon)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Beacon added", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed to add beacon", "Error adding document", e);
                    }
                });
    }

    public void switchToHome()
    {
        Intent intent = new Intent(this, StartAdventour.class);
        startActivity(intent);
        finish();
    }
}