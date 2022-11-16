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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    int numLikeShards = 10;
    boolean fromPassport = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_post);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Bundle extras = getIntent().getExtras();
        if (extras != null && (boolean) extras.get("fromPassport")) {
            fromPassport = true;
        }

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

        BeaconPostAdapter BeaconPostAdapter;
        if (fromPassport) {
            beaconTitleEditText.setText((String) extras.get("beaconTitle"));
            beaconIntroEditText.setText((String) extras.get("beaconIntro"));
            BeaconPostAdapter = new BeaconPostAdapter(this, GlobalVars.beaconModelArrayListPassport);
        } else {
            BeaconPostAdapter = new BeaconPostAdapter(this, GlobalVars.beaconModelArrayList);
        }

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

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && (boolean) extras.get("fromPassport")) {
            Intent intent = new Intent(this, Passport.class);
            intent.putExtra("fromBeaconPost", true);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, Congratulations.class);
            startActivity(intent);
            finish();
        }
    }

    public void postToBeaconBoard(String adventourId)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> newBeacon = new HashMap<>();

        if (fromPassport) {
            newBeacon.put("dateCreated", new Timestamp(new Date()));
            newBeacon.put("dateUpdated", new Timestamp(new Date()));
            newBeacon.put("locations", GlobalVars.adventourFSQIdsPassport);
            newBeacon.put("numLocations", GlobalVars.adventourFSQIdsPassport.size());
            newBeacon.put("uid", user.getUid());
            newBeacon.put("title", beaconTitleEditText.getText().toString());
            newBeacon.put("intro", beaconIntroEditText.getText().toString());
            newBeacon.put("isPrivate", isPrivate.isChecked());
            newBeacon.put("locationDescriptions", GlobalVars.locationDescriptions);
            newBeacon.put("beaconLocation", GlobalVars.selectedLocationPassport);
        } else {
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
        }

        DocumentReference beaconRef = db.collection("Beacons").document(adventourId);
        createLikeCounter(beaconRef, numLikeShards);

        beaconRef.set(newBeacon)
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

        if (fromPassport) {
            addBeacon.put("dateCreated", new Timestamp(new Date()));
            addBeacon.put("dateUpdated", new Timestamp(new Date()));
            addBeacon.put("locations", GlobalVars.adventourFSQIdsPassport);
            addBeacon.put("numLocations", GlobalVars.adventourFSQIdsPassport.size());
            addBeacon.put("uid", user.getUid());
            addBeacon.put("title", beaconTitleEditText.getText().toString());
            addBeacon.put("intro", beaconIntroEditText.getText().toString());
            addBeacon.put("isPrivate", isPrivate.isChecked());
            addBeacon.put("locationDescriptions", GlobalVars.locationDescriptions);
            addBeacon.put("beaconLocation", GlobalVars.selectedLocationPassport);
            addBeacon.put("adventourId", adventourId);
        } else {
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
            addBeacon.put("adventourId", adventourId);
        }

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

        // If adventour is in db already or not
        if (fromPassport) {
            newAdventour.put("dateCreated", new Timestamp(new Date()));
            newAdventour.put("locations", GlobalVars.adventourFSQIdsPassport);
            newAdventour.put("numLocations", GlobalVars.adventourFSQIdsPassport.size());
            newAdventour.put("beaconLocation", GlobalVars.selectedLocationPassport);
            newAdventour.put("isBeacon", true);

            storeBeacon((String) getIntent().getExtras().get("adventourID"));
            postToBeaconBoard((String) getIntent().getExtras().get("adventourID"));
        } else {
            newAdventour.put("dateCreated", new Timestamp(new Date()));
            newAdventour.put("locations", GlobalVars.adventourFSQIds);
            newAdventour.put("numLocations", GlobalVars.adventourFSQIds.size());
            newAdventour.put("beaconLocation", GlobalVars.selectedLocation);
            newAdventour.put("isBeacon", true);

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
        //TODO: for future versions of the app: it would be nice to store categories here so they can be displayed on the prevAdventour/beacon cards
        // and users could potentially filter by categories.
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
                        Log.d("Beacon Post", "DocumentSnapshot data: " + document.getData());
                        authorTextView.setText(document.getString("nickname"));

                        // Set androidPfpRef for Profile Picture
                        if (document.get("androidPfpRef") != null)
                        {
                            androidPfpRef = toIntExact((long) document.get("androidPfpRef"));
                        } else if (document.get("iosPfpRef") != null) {
                            androidPfpRef = AdventourUtils.iOSToAndroidPfpRef((String)document.get("iosPfpRef"));
                        } else {
                            androidPfpRef = 6; // Default PFP Pic
                        }

                        // Set image resource according to androidPfpRef
                        switch (androidPfpRef) {
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
                        Log.d("Beacon Post", "No such document");
                    }
                } else {
                    Log.d("Beacon Post", "get failed with ", task.getException());
                }
            }
        });
    }

    public Task<Void> createLikeCounter(final DocumentReference beaconRef, final int numLikeShards)
    {
        // Initialize the counter document, then initialize each shard.
        return beaconRef.set(new LikeCounter(numLikeShards))
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        List<Task<Void>> tasks = new ArrayList<>();

                        // Initialize each shard with count=0
                        for (int i = 0; i < numLikeShards; i++) {
                            Task<Void> makeShard = beaconRef.collection("likeShards")
                                    .document(String.valueOf(i))
                                    .set(new LikeShard(0));

                            tasks.add(makeShard);
                        }

                        return Tasks.whenAll(tasks);
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