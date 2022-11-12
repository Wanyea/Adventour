package com.adventour.android;

import static java.lang.Math.toIntExact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

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

public class BeaconPost extends AppCompatActivity {

    TextView beaconPostDate, authorTextView;
    ImageButton postBeaconButton;
    ImageView authorImageView;
    EditText beaconTitleEditText, beaconIntroEditText;
    Switch isPrivate;

    FirebaseAuth auth;
    FirebaseUser user;

    int androidPfpRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_post);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        beaconPostDate = (TextView) findViewById(R.id.beaconPostDate);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        postBeaconButton = (ImageButton) findViewById(R.id.postBeaconButton);
        beaconTitleEditText = (EditText) findViewById(R.id.beaconTitleEditText);
        beaconIntroEditText = (EditText) findViewById(R.id.beaconIntroEditText);

        isPrivate = (Switch) findViewById(R.id.privateSwitch);

        authorImageView = (ImageView) findViewById(R.id.authorImageView);

        beaconPostDate.setText(AdventourUtils.formatBirthdateFromDatabase(new Timestamp(new Date())));
        getUserNickname();
        
        AlertDialog.Builder postBeaconAlert = new AlertDialog.Builder(this);
        postBeaconAlert.setMessage("Are you sure you want to post this beacon?");
        postBeaconAlert.setCancelable(true);

        postBeaconAlert.setPositiveButton(
                R.string.Yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("ConfirmDialogue", "Yes/No dialogue opened");
                        storeAdventour();
                        switchToHome();
                    }
                });
        postBeaconAlert.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

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
               AlertDialog alert = postBeaconAlert.create();
               alert.show();
           }
        });
    }

    public void postToBeaconBoard(String adventourId)
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
        newBeacon.put("beaconLocation", GlobalVars.selectedLocation);

        db.collection("Beacons")
                .document(adventourId)
                .set(newBeacon)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void v) {
                        Log.d("Beacon Posted", "DocumentSnapshot written with ID: " + v);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed to post beacon", "Error adding document", e);
                    }
                });
    }

    public void storeBeacon(String adventourId)
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
        addBeacon.put("beaconLocation", GlobalVars.selectedLocation);

        db.collection("Adventourists")
                .document(user.getUid())
                .collection("beacons")
                .document(adventourId)
                .set(addBeacon)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void v) {
                        Log.d("Beacon added", "DocumentSnapshot written with ID: " + v);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed to add beacon", "Error adding document", e);
                    }
                });
    }

    public void storeAdventour()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> newAdventour= new HashMap<>();

        // Get a reference to the user
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());

        // Check if user document exists. If they do in this instance, attach users nickname and profile pic.
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            private static final String TAG = "BEACON POST";

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        newAdventour.put("nickname", document.getString("nickname"));
                        String adventourId = document.getId();
                        Log.d(TAG, "adventourID: " + adventourId);
                        Log.d(TAG, "Confirming that adventourID is printed before continuing");
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
        newAdventour.put("adventourLocations", GlobalVars.adventourLocations);
        newAdventour.put("beaconLocation", GlobalVars.selectedLocation);
        newAdventour.put("isBeacon", true);

        //TODO: for future versions of the app: it would be nice to store categories here so they can be displayed on the prevAdventour/beacon cards
        // and users could potentially filter by categories.

        db.collection("Adventourists")
                .document(user.getUid())
                .collection("adventours")
                .add(newAdventour)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("New Adventour added", "DocumentSnapshot written with ID: " + documentReference.getId());
                        storeBeacon(documentReference.getId());
                        postToBeaconBoard(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed to add Adventour", "Error adding document", e);
                    }
                });
    }

    public void getUserNickname() {
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
                        Log.d("BEACON POST", "DocumentSnapshot data: " + document.getData());
                        authorTextView.setText(document.getString("nickname"));

                        androidPfpRef = toIntExact((long)document.get("androidPfpRef"));

                        switch (androidPfpRef)
                        {
                            // Set profile pic image to Cheetah
                            case 0:
                                authorImageView.setImageResource(R.drawable.ic_profpic_cheetah);
                                    break;

                             // Set profile pic image to Elephant
                            case 1:
                                authorImageView.setImageResource(R.drawable.ic_profpic_elephant);
                                    break;

                            // Set profile pic image to Ladybug
                            case 2:
                                authorImageView.setImageResource(R.drawable.ic_profpic_ladybug);
                                    break;

                            // Set profile pic image to Monkey
                            case 3:
                                authorImageView.setImageResource(R.drawable.ic_profpic_monkey);
                                    break;

                            // Set profile pic image to Fox
                            case 4:
                                authorImageView.setImageResource(R.drawable.ic_profpic_fox);
                                    break;

                            // Set profile pic image to Penguin
                            case 5:
                                authorImageView.setImageResource(R.drawable.ic_profpic_penguin);
                                    break;
                            default:
                                authorImageView.setImageResource(R.drawable.ic_user_icon);
                        }
                    } else {
                        Log.d("BEACON POST", "No such document");
                    }
                } else {
                    Log.d("BEACON POST", "get failed with ", task.getException());
                }
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