package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import android.widget.LinearLayout;


import android.widget.TextView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Passport extends AppCompatActivity {
    
    private static final String TAG = "PassportActivity";

    ImageButton imageButton;

    TextView nicknameTextView, birthdateTextView, mantraTextView;

    FirebaseAuth auth;
    FirebaseUser user;


    Context context = this;
    LinearLayout linearLayout, linearLayout2;

    ActionBar actionBar;

    ArrayList<String> queryString;

    RecyclerView PreviousAdventourRV, BeaconPostRV;

    PreviousAdventourAdapter previousAdventourAdapter;
    BeaconsAdapter beaconsAdapter;

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
        PreviousAdventourRV = findViewById(R.id.previousAdventourRV);
        BeaconPostRV = findViewById(R.id.beaconPostsRV);

        queryString = new ArrayList<>();

        handleAuth();
        populatePassport();

        Log.d("OnCreate", "Data length: " + String.valueOf(GlobalVars.previousAdventourArrayList.size()));
        PreviousAdventourRV.setNestedScrollingEnabled(true);
        previousAdventourAdapter = new PreviousAdventourAdapter(context, GlobalVars.previousAdventourArrayList);
        LinearLayoutManager adventourLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        PreviousAdventourRV.setLayoutManager(adventourLinearLayoutManager);
        PreviousAdventourRV.setAdapter(previousAdventourAdapter);
        getPreviousAdventours();

        BeaconPostRV.setNestedScrollingEnabled(true);
        beaconsAdapter = new BeaconsAdapter(context, GlobalVars.userBeaconsArrayList);
        LinearLayoutManager beaconsLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        BeaconPostRV.setLayoutManager(beaconsLinearLayoutManager);
        BeaconPostRV.setAdapter(previousAdventourAdapter);
        getBeaconPosts();


        // Action Bar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchToPassportMoreInfo();
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hamburger_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.adventourTOS:
                openAdventourTOS(); // Prompts user to navigate to Adventour TOS on our site.
                return true;

            case R.id.logOut:
                // Build AlertDialog that will alert users when they try to log out account.
                AlertDialog.Builder logOutAlertBuilder = new AlertDialog.Builder(this);
                logOutAlertBuilder.setMessage("Are you sure you want to log out?");
                logOutAlertBuilder.setCancelable(true);

                logOutAlertBuilder.setPositiveButton(
                        R.string.Yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                FirebaseAuth.getInstance().signOut();
                                switchToLoggedOut();
                            }
                        });

                logOutAlertBuilder.setNegativeButton(
                        R.string.No,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog logOutAlert = logOutAlertBuilder.create();
                logOutAlert.show();
                return true;

            case R.id.deleteAccount:
                // Build AlertDialog that will alert users when they try to delete their account.
                AlertDialog.Builder deleteAccountAlertBuilder = new AlertDialog.Builder(this);
                deleteAccountAlertBuilder.setMessage("Are you sure you want to delete your account?");
                deleteAccountAlertBuilder.setCancelable(true);

                deleteAccountAlertBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                // DELETE USER DOCUMENT IN FIREBASE.
                                // switchToLoggedOut();
                            }
                        });

                deleteAccountAlertBuilder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog deleteAccountAlert = deleteAccountAlertBuilder.create();
                deleteAccountAlert.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void populatePassport()
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
                        Log.d("birthdate DT precast: ", ((Timestamp)document.get("birthdate")).getClass().toString());
                        birthdateTextView.setText(AdventourUtils.formatBirthdateFromDatabase(((Timestamp)document.get("birthdate"))));
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

    public void getPreviousAdventours()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the user
        Log.d("getPrevAdventours", "Calling database...");
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());
        documentRef.collection("adventours")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("getPrevAdventours", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("getPrevAdventours", "Completed calling database...");
                        if (task.isSuccessful())
                        {
                            Log.d("getPrevAdventours", "Documents retrieved!");
                            for (QueryDocumentSnapshot adventour : task.getResult())
                            {

                                Map<String, Object> allData = new HashMap<>();
                                allData = adventour.getData();
                                allData.put("documentID", adventour.getId());

                                Log.d("getPrevAdventours", allData.toString());
                                ArrayList<String> locations = (ArrayList<String>) allData.get("locations");
                                Log.d("getPrevAdventours", locations.toString());

                                JSONObject requestBody = new JSONObject();

                                try
                                {
                                    requestBody.put("ids", new JSONArray(locations));
                                    requestBody.put("uid", user.getUid());
                                } catch (JSONException e) {
                                    Log.e("getPrevAdventours Error", e.toString());
                                }

                                try {
                                    URL url = new URL("https://adventour-183a0.uc.r.appspot.com/get-foursquare-places");
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                                    conn.setDoOutput(true);
                                    conn.setInstanceFollowRedirects(false);
                                    conn.setRequestMethod("POST");
                                    conn.setRequestProperty("Content-Type", "application/json");
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setUseCaches(false);

                                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                                    wr.writeBytes(requestBody.toString());
                                    wr.flush();
                                    wr.close();
                                    requestBody = null;

                                    System.out.println("\nSending 'POST' request to URL : " + url);

                                    InputStream it = conn.getInputStream();
                                    InputStreamReader inputs = new InputStreamReader(it);

                                    BufferedReader in = new BufferedReader(inputs);
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }

                                    in.close();
                                    JSONObject responseData = new JSONObject(response.toString());
                                    JSONArray results = (JSONArray) responseData.get("results");
                                    allData.put("locations", results);
                                    GlobalVars.previousAdventourArrayList.add(new PreviousAdventourModel(allData));
                                    Log.d("getPrevAdventours: ", GlobalVars.previousAdventourArrayList.get(GlobalVars.previousAdventourArrayList.size()-1).getFirstLocation());

                                    previousAdventourAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("getPrevAdventours", e.toString());
                                }

                            }

                        }
                    }
                });
    }

    public void getBeaconPosts()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the user
        Log.d("getPrevAdventours", "Calling database...");
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());
        documentRef.collection("beacons")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("getPrevAdventours", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("getPrevAdventours", "Completed calling database...");
                        if (task.isSuccessful())
                        {
                            Log.d("getPrevAdventours", "Documents retrieved!");
                            for (QueryDocumentSnapshot beacons : task.getResult())
                            {

                                Map<String, Object> allData = new HashMap<>();
                                allData = beacons.getData();
                                allData.put("documentID", beacons.getId());

                                Log.d("BEACON DATA", "all beacon data: " + allData);
                                ArrayList<String> locations = (ArrayList<String>) allData.get("locations");
                                Log.d("getPrevAdventours", locations.toString());

                                JSONObject requestBody = new JSONObject();

                                try
                                {
                                    requestBody.put("ids", new JSONArray(locations));
                                    requestBody.put("uid", user.getUid());
                                } catch (JSONException e) {
                                    Log.e("getPrevAdventours Error", e.toString());
                                }

                                try {
                                    URL url = new URL("https://adventour-183a0.uc.r.appspot.com/get-foursquare-places");
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                                    conn.setDoOutput(true);
                                    conn.setInstanceFollowRedirects(false);
                                    conn.setRequestMethod("POST");
                                    conn.setRequestProperty("Content-Type", "application/json");
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setUseCaches(false);

                                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                                    wr.writeBytes(requestBody.toString());
                                    wr.flush();
                                    wr.close();
                                    requestBody = null;

                                    System.out.println("\nSending 'POST' request to URL : " + url);

                                    InputStream it = conn.getInputStream();
                                    InputStreamReader inputs = new InputStreamReader(it);

                                    BufferedReader in = new BufferedReader(inputs);
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }

                                    in.close();
                                    JSONObject responseData = new JSONObject(response.toString());
                                    JSONArray results = (JSONArray) responseData.get("results");
                                    allData.put("locations", results);
                                    GlobalVars.userBeaconsArrayList.add(new BeaconsModel(allData));
                                    Log.d("getPrevAdventours: ", GlobalVars.previousAdventourArrayList.get(GlobalVars.previousAdventourArrayList.size()-1).getFirstLocation());

                                    previousAdventourAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("getPrevAdventours", e.toString());
                                }

                            }

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

    public void openAdventourTOS()
    {
        // Prompt user to navigate to Adventour TOS on our site.
    }

}