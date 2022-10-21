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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Passport extends AppCompatActivity {
    
    private static final String TAG = "PassportActivity";

    ImageButton imageButton;

    TextView nicknameTextView, birthdateTextView, mantraTextView;

    FirebaseAuth auth;
    FirebaseUser user;


    Context context;
    LinearLayout linearLayout, linearLayout2;

    Menu hamburgerMenu;

    ActionBar actionBar;

    ArrayList<String> queryString;


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

        queryString = new ArrayList<>();

        handleAuth();
        populatePassportTextViews();
        getPreviousAdventours();

//        RecyclerView PreviousAdventourRV = findViewById(R.id.previousAdventourRV);
//        PreviousAdventourRV.setNestedScrollingEnabled(true);
//
//        PreviousAdventourAdapter previousAdventourAdapter = new PreviousAdventourAdapter(this, GlobalVars.prevArrayList);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//
//        PreviousAdventourRV.setLayoutManager(linearLayoutManager);
//        PreviousAdventourRV.setAdapter(previousAdventourAdapter);


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

        // Build AlertDialog that will alert users when they try to log out account.
        AlertDialog.Builder logOutAlertBuilder = new AlertDialog.Builder(this);
        logOutAlertBuilder.setMessage("Are you sure you want to log out?");
        logOutAlertBuilder.setCancelable(true);

        logOutAlertBuilder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        FirebaseAuth.getInstance().signOut();
                        switchToLoggedOut();
                    }
                });

        logOutAlertBuilder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

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

        switch(item.getItemId())
        {
            case R.id.adventourTOS:
                openAdventourTOS(); // Prompts user to navigate to Adventour TOS on our site.
                return true;

            case R.id.logOut:
                AlertDialog logOutAlert = logOutAlertBuilder.create();
                logOutAlert.show();
                return true;

            case R.id.deleteAccount:
                AlertDialog deleteAccountAlert = deleteAccountAlertBuilder.create();
                deleteAccountAlert.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void newLitBeaconCard() {

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

    public void getPreviousAdventours()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<PreviousAdventourModel> previousAdventours = new ArrayList<>();

        // Get a reference to the user
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());
        documentRef.collection("adventours")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot adventour : task.getResult())
                            {
                                queryString.addAll((Collection<? extends String>) adventour.get("locations"));
                                getLocationName(queryString.toString().replaceAll("\\s+","")); //maybe spaces are causing weird api response?
                                queryString.clear();
                                Log.d(TAG, adventour.getId() + " => " + adventour.get("locations"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void getLocationName(String queryString)
    {
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("uid", user.getUid());
            // Log.d("0", queryString);
            jsonBody.put("ids", "4b05869af964a520966722e3");

        } catch (JSONException e) {
            Log.e("Passport", "exception", e);
        }

        try {

            // Call API with user defined location and sentiments
            URL url = new URL("https://adventour-183a0.uc.r.appspot.com/get-adventour-place");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(jsonBody.toString());
            wr.flush();
            wr.close();
            jsonBody = null;

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
            Log.d("RESPONSE DATA", responseData.toString());
//            try {
//                JSONObject data = (JSONObject) responseData.get("data");
//                currentFSQId = data.get("fsq_id").toString();
//                name = data.get("name").toString();
//                rating = Float.parseFloat(data.get("rating").toString()) / 2;
//
//                try {
//                    tel = data.get("tel").toString();
//                } catch (Exception e) {
//                    tel = "N/A";
//                    Log.e("No tel for location", "Exception", e);
//                }
//
//                try {
//                    website = data.get("website").toString();
//                } catch (Exception e) {
//                    website = "N/A";
//                    Log.e("No web for location", "Exception", e);
//                }
//
//                try {
//                    description = data.get("description").toString();
//                } catch (Exception e) {
//                    description = "No description available for this location... ";
//                    Log.e("No des for location", "Exception", e);
//                }
//
//                Log.d("START ADVENTOUR", currentFSQId + " " + name + " " + rating + " " + tel + " " + website + " " + description);
//            } catch (Exception e) {
//                Log.e("Exception" , e.toString());
//            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
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