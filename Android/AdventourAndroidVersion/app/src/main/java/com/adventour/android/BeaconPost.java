package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BeaconPost extends AppCompatActivity {

    TextView beaconPostDate;
    FloatingActionButton postBeaconButton;
    ImageButton editSaveButton;
    EditText beaconTitleEditText, beaconIntroEditText;
    boolean isEditMode = true;
    String titleString, introString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_post);

        beaconPostDate = (TextView) findViewById(R.id.beaconPostDate);
        postBeaconButton = (FloatingActionButton) findViewById(R.id.postBeaconButton);
        editSaveButton = (ImageButton) findViewById(R.id.editSaveButton);
        beaconTitleEditText = (EditText) findViewById(R.id.beaconTitleEditText);
        beaconIntroEditText = (EditText) findViewById(R.id.beaconIntroEditText);

        final Calendar today = Calendar.getInstance();
        beaconPostDate.setText(AdventourUtils.formatBirthdateForDB(today));

        RecyclerView beaconPostRV = findViewById(R.id.beaconPostRV);
        beaconPostRV.setNestedScrollingEnabled(true);

        BeaconPostAdapter BeaconPostAdapter = new BeaconPostAdapter(this, GlobalVars.beaconModelArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        beaconPostRV.setLayoutManager(linearLayoutManager);
        beaconPostRV.setAdapter(BeaconPostAdapter);

        postBeaconButton.setOnClickListener(new View.OnClickListener()
        {
           @Override
           public void onClick(View view)
           {
               postBeacon();
               switchToHome();
           }
        });

        editSaveButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               if(isEditMode)
               {
                   beaconTitleEditText.setFocusable(false);
                   beaconIntroEditText.setFocusable(false);
                   editSaveButton.setBackgroundResource(R.drawable.ic_edit_icon);
                   titleString = beaconTitleEditText.getText().toString();
                   introString = beaconIntroEditText.getText().toString();
               } else {
                   beaconTitleEditText.setFocusableInTouchMode(true);
                   beaconIntroEditText.setFocusableInTouchMode(true);
                   editSaveButton.setBackgroundResource(R.drawable.ic_save_icon);

               }

               isEditMode = !isEditMode;

           }
        });
    }

    public void postBeacon()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Calendar today = Calendar.getInstance();
        Map<String, Object> newBeacon = new HashMap<>();
        newBeacon.put("dateCreated", AdventourUtils.formatBirthdateForDB(today));
        newBeacon.put("locations", GlobalVars.adventourFSQIds);
        newBeacon.put("numLocations", GlobalVars.adventourFSQIds.size());

        //TODO: fields for beacon title and intro???

        db.collection("Adventourists")
                .document(user.getUid())
                .collection("Beacons")
                .add(newBeacon)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Beacon Post", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Beacon Post", "Error adding document", e);
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