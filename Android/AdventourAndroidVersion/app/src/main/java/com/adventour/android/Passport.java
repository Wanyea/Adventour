package com.adventour.android;

import static java.lang.Math.toIntExact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import android.widget.ImageView;


import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Passport extends AppCompatActivity {

    private static final String TAG = "PassportActivity";
    public String userNickname;

    ImageButton imageButton, hamburgerMenuImageButton;

    TextView nicknameTextView, birthdateTextView, mantraTextView, noPrevAdventours, noPrevBeacons, adventourTOS, logOut, privacyPolicy;

    FirebaseAuth auth;
    FirebaseUser user;

    Context context = this;

    ActionBar actionBar;

    ArrayList<String> queryString;

    RecyclerView PreviousAdventourRV, BeaconPostRV;

    PreviousAdventourAdapter previousAdventourAdapter;
    BeaconsAdapter beaconsAdapter;

    ProgressBar passportCardProgressBar, previousAdventoursProgressBar, beaconPostsProgressBar;
    ImageView cakeIconImageView, profPicImageView;
    TextView myLitBeaconsHeader;

    View outsideView;

    int androidPfpRef;

    boolean isHamburgerMenuOpen = false;

    ConstraintLayout hamburgerMenuPopup;

    HashMap<String, String> isSwitchActive = new HashMap<>();
    int distance = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);

        if (getIntent().getSerializableExtra("isSwitchActive") != null)
        {
            isSwitchActive = (HashMap) getIntent().getSerializableExtra("isSwitchActive");
            Log.d("InProgress isSwitch", isSwitchActive.toString());
        }

        if (getIntent().getSerializableExtra("distance") != null)
        {
            distance = (int) getIntent().getSerializableExtra("distance");
            Log.d("InProgress distance", String.valueOf(distance));
        }

        // Dump GlobalVars
        GlobalVars.beaconBoardArrayList.clear();
        GlobalVars.previousAdventourArrayList.clear();
        GlobalVars.userBeaconsArrayList.clear();
        clearPassportGlobalVals();

        context = getApplicationContext();

        imageButton = (ImageButton) findViewById(R.id.imageButton);

        nicknameTextView = (TextView) findViewById(R.id.nicknameTextView);
        birthdateTextView = (TextView) findViewById(R.id.birthdateTextView);
        mantraTextView = (TextView) findViewById(R.id.mantraTextView);
        noPrevAdventours = (TextView) findViewById(R.id.takeAnAdventourTextView);
        noPrevBeacons = (TextView) findViewById(R.id.postABeaconTextView);
        adventourTOS = (TextView) findViewById(R.id.adventourTOS);
        logOut = (TextView) findViewById(R.id.logOut);
        privacyPolicy = (TextView) findViewById(R.id.privacyPolicy);

        PreviousAdventourRV = findViewById(R.id.previousAdventourRV);
        BeaconPostRV = findViewById(R.id.beaconPostsRV);

        passportCardProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        previousAdventoursProgressBar = (ProgressBar) findViewById(R.id.previousAdventoursProgressBar);
        beaconPostsProgressBar = (ProgressBar) findViewById(R.id.beaconPostsProgressBar);

        cakeIconImageView = (ImageView) findViewById(R.id.cakeIconImageView);
        profPicImageView = (ImageView) findViewById(R.id.profPicImageView);
        myLitBeaconsHeader = (TextView) findViewById(R.id.myLitBeaconsHeader);

        hamburgerMenuPopup = (ConstraintLayout) findViewById(R.id.hamburgerMenuPopup);

        hamburgerMenuImageButton = (ImageButton) findViewById(R.id.hamburgerMenuImageButton);

        queryString = new ArrayList<>();

        outsideView = (View) findViewById(R.id.outsideView);

        handleAuth();
        populatePassport();

        PreviousAdventourRV.setNestedScrollingEnabled(true);
        previousAdventourAdapter = new PreviousAdventourAdapter(context, GlobalVars.previousAdventourArrayList);
        LinearLayoutManager adventourLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        PreviousAdventourRV.setLayoutManager(adventourLinearLayoutManager);
        PreviousAdventourRV.setAdapter(previousAdventourAdapter);
        getPreviousAdventours();

        PreviousAdventourRV.setNestedScrollingEnabled(false);
        PreviousAdventourRV.addOnItemTouchListener(
                new PreviousAdventourClickListener(this, PreviousAdventourRV, new PreviousAdventourClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("PreviousAdventourClick", "onItemClicked triggered, " + GlobalVars.previousAdventourArrayList.get(position).getAdventourId());
                        switchToAdventourSummary(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.d("PreviousAdventourClick", "onLongItemClicked triggered");
                    }
                })
        );

        BeaconPostRV.setNestedScrollingEnabled(true);
        beaconsAdapter = new BeaconsAdapter(context, GlobalVars.userBeaconsArrayList);
        LinearLayoutManager beaconsLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        BeaconPostRV.setLayoutManager(beaconsLinearLayoutManager);
        BeaconPostRV.setAdapter(beaconsAdapter);
        getBeaconPosts();

        // Action Bar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchToEditPassport();
            }
        });

        hamburgerMenuImageButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v)
           {
               if (isHamburgerMenuOpen)
               {
                   hamburgerMenuPopup.setVisibility(View.GONE);
                   outsideView.setVisibility(View.GONE);
               } else {
                   hamburgerMenuPopup.setVisibility(View.VISIBLE);
                   outsideView.setVisibility(View.VISIBLE);
               }

               isHamburgerMenuOpen = !isHamburgerMenuOpen;
           }
        });

        adventourTOS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                openAdventourTOS();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                AlertDialog.Builder logOutAlertBuilder = new AlertDialog.Builder(Passport.this);
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
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                openPrivacyPolicy();
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
                    Intent startAdventourIntent = new Intent(getApplicationContext(), StartAdventour.class);
                    startAdventourIntent.putExtra("isSwitchActive", isSwitchActive);
                    startAdventourIntent.putExtra("distance", distance);
                    startActivity(startAdventourIntent);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.beacons:
                    Intent beaconsIntent = new Intent(getApplicationContext(), Beacons.class);
                    beaconsIntent.putExtra("isSwitchActive", isSwitchActive);
                    beaconsIntent.putExtra("distance", distance);
                    startActivity(beaconsIntent);
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });

        outsideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hamburgerMenuPopup.setVisibility(View.GONE);
                outsideView.setVisibility(View.GONE);
                isHamburgerMenuOpen = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
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
                        userNickname = document.getString("nickname");
                        birthdateTextView.setText(AdventourUtils.formatBirthdateFromDatabase(((Timestamp)document.get("birthdate"))));
                        mantraTextView.setText(document.getString("mantra"));

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
                                profPicImageView.setImageResource(R.drawable.ic_profpic_cheetah);
                                break;

                            // Set profile pic image to Elephant
                            case 1:
                                profPicImageView.setImageResource(R.drawable.ic_profpic_elephant);
                                break;

                            // Set profile pic image to Ladybug
                            case 2:
                                profPicImageView.setImageResource(R.drawable.ic_profpic_ladybug);
                                break;
                            // Set profile pic image to Monkey
                            case 3:
                                profPicImageView.setImageResource(R.drawable.ic_profpic_monkey);
                                break;
                            // Set profile pic image to Fox
                            case 4:
                                profPicImageView.setImageResource(R.drawable.ic_profpic_fox);
                                break;
                            // Set profile pic image to Penguin
                            case 5:
                                profPicImageView.setImageResource(R.drawable.ic_profpic_penguin);
                                break;
                            default:
                                profPicImageView.setImageResource(R.drawable.ic_user_icon);
                        }

                        // Set visibility
                        passportCardProgressBar.setVisibility(View.INVISIBLE);
                        profPicImageView.setVisibility(View.VISIBLE);
                        nicknameTextView.setVisibility(View.VISIBLE);
                        cakeIconImageView.setVisibility(View.VISIBLE);
                        birthdateTextView.setVisibility(View.VISIBLE);
                        mantraTextView.setVisibility(View.VISIBLE);
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
                                        Log.d("size: ", String.valueOf(GlobalVars.previousAdventourArrayList.size()));

                                        previousAdventourAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("getPrevAdventours", e.toString());
                                    }
                            }

                            Log.d("numOfAdventours", String.valueOf(GlobalVars.previousAdventourArrayList.size()));

                            previousAdventoursProgressBar.setVisibility(View.INVISIBLE);

                            // Set placeholder if user hasn't taken any Adventours.
                            if(GlobalVars.previousAdventourArrayList.size() == 0)
                            {
                                noPrevAdventours.setVisibility(View.VISIBLE);
                            } else {
                                noPrevAdventours.setVisibility(View.INVISIBLE);
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
        Log.d("getBeaconPosts", "Calling database...");
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());
        documentRef.collection("beacons")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("getBeaconPosts", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("getBeaconPosts", "Completed calling database...");
                        if (task.isSuccessful())
                        {
                            Log.d("getBeaconPosts", "Documents retrieved!");
                            for (QueryDocumentSnapshot beacons : task.getResult())
                            {
                                    Map<String, Object> allData = new HashMap<>();
                                    allData = beacons.getData();
                                    allData.put("documentID", beacons.getId());

                                    Log.d("BEACON DATA", "all beacon data: " + allData);
                                    ArrayList<String> locations = (ArrayList<String>) allData.get("locations");
                                    Log.d("getBeaconPosts", locations.toString());

                                    JSONObject requestBody = new JSONObject();

                                    try
                                    {
                                        requestBody.put("ids", new JSONArray(locations));
                                        requestBody.put("uid", user.getUid());
                                    } catch (JSONException e) {
                                        Log.e("getBeaconPosts Error", e.toString());
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
                                        GlobalVars.userBeaconsArrayList.add(new BeaconsModel(allData, userNickname, androidPfpRef));

                                        beaconsAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("getBeaconPosts", e.toString());
                                    }
                            }

                            Log.d("numOfUserBeacons", String.valueOf(GlobalVars.userBeaconsArrayList.size()));

                            beaconPostsProgressBar.setVisibility(View.INVISIBLE);

                            // Set placeholder if user hasn't posted any Beacons.
                            if(GlobalVars.userBeaconsArrayList.size() == 0)
                            {
                                noPrevBeacons.setVisibility(View.VISIBLE);

                                // Update constraint
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myLitBeaconsHeader.getLayoutParams();
                                params.topMargin = 120;
                            } else {
                                noPrevBeacons.setVisibility(View.INVISIBLE);

                                // Update constraint
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myLitBeaconsHeader.getLayoutParams();
                                params.topMargin = 60;
                            }
                        }
                    }
                });
    }

    public void switchToEditPassport()
    {
        Intent intent = new Intent(this, EditPassport.class);
        startActivity(intent);
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
        String url = "https://adventour.app/terms-of-service";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void openPrivacyPolicy()
    {
        String url = "https://adventour.app/privacy-policy";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void switchToAdventourSummary(int position) {
        final String TAG = "makingAdventourSummary";
        Context c = this;
        String adventourID = GlobalVars.previousAdventourArrayList.get(position).getAdventourId();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the user
        Log.d(TAG, "Calling database...");
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());
        documentRef.collection("adventours")
                .document(adventourID)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed calling database");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d(TAG, "Completed calling database");
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "Documents retrieved!");

                            DocumentSnapshot documentSnapshot = task.getResult();
                            GlobalVars.adventourFSQIdsPassport = (ArrayList<String>) documentSnapshot.get("locations");
                            GlobalVars.selectedLocationPassport = documentSnapshot.getString("beaconLocation");

                            Log.d(TAG, GlobalVars.adventourFSQIdsPassport.size() + " adventourFSQIds:" + GlobalVars.adventourFSQIdsPassport);

                            JSONArray results = new JSONArray();
                            Map<String, Object> allData = new HashMap<>();
                            JSONObject requestBody = new JSONObject();

                            try
                            {
                                requestBody.put("ids", new JSONArray(GlobalVars.adventourFSQIdsPassport));
                                requestBody.put("uid", user.getUid());
                            } catch (JSONException e) {
                                Log.e(TAG, e.toString());
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
//                                System.out.println(responseData);
                                results = (JSONArray) responseData.get("results");
//                                allData.put("locations", results);
//                                System.out.println(allData);

                                for (int i = 0; i < GlobalVars.adventourFSQIdsPassport.size(); i++) {
                                    JSONObject obj = (JSONObject) results.get(i);
                                    Log.d(TAG, obj.toString());

                                    String description = "";
                                    try {
                                        description = obj.get("description").toString();
                                    } catch(Exception e) {
                                        description = "No description available for this location... ";
                                        Log.e("No des for location", "Exception", e);
                                    }

                                    GlobalVars.adventourLocationsPassport.add(new AdventourSummaryModel(obj.getString("name"), description));
                                    Log.d(TAG, "adventourLocations: " + GlobalVars.adventourLocationsPassport.toString());

                                    StartAdventour.LocationImages locationImages = getLocationImages(obj.getJSONArray("photos"));
                                    GlobalVars.beaconModelArrayListPassport.add(new BeaconPostModel(obj.getString("name"), Float.parseFloat(obj.get("rating").toString()) / 2, ((JSONObject)obj.get("location")).getString("formatted_address"), description, locationImages));
                                    Log.d(TAG, "beaconModelArrayList" + GlobalVars.beaconModelArrayListPassport.toString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, e.toString());
                            }

                            Intent intent = new Intent(c, AdventourSummary.class);
                            intent.putExtra("fromPassport", true);
                            intent.putExtra("adventourID", adventourID);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    public StartAdventour.LocationImages getLocationImages(JSONArray photos) {
        StartAdventour.LocationImages locationImages = new StartAdventour.LocationImages();
        URL imageOneURL, imageTwoURL, imageThreeURL;
        HttpURLConnection connectionOne, connectionTwo, connectionThree;
        InputStream inputOne, inputTwo, inputThree;
        Bitmap bitmap;

        try {
            // Try to get first location image.
            if (photos.length() > 2) {
                String firstPrefix = photos.getJSONObject(0).get("prefix").toString();
                String firstSuffix = photos.getJSONObject(0).get("suffix").toString();

                String secondPrefix = photos.getJSONObject(1).get("prefix").toString();
                String secondSuffix = photos.getJSONObject(1).get("suffix").toString();

                String thirdPrefix = photos.getJSONObject(2).get("prefix").toString();
                String thirdSuffix = photos.getJSONObject(2).get("suffix").toString();

                imageOneURL = new URL(firstPrefix + "original" + firstSuffix);
                connectionOne = (HttpURLConnection) imageOneURL.openConnection();
                connectionOne.setDoInput(true);
                connectionOne.connect();
                inputOne = connectionOne.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputOne);
                locationImages.locationOne = bitmap;

                imageTwoURL = new URL(secondPrefix + "original" + secondSuffix);
                connectionTwo = (HttpURLConnection) imageTwoURL.openConnection();
                connectionTwo.setDoInput(true);
                connectionTwo.connect();
                inputTwo = connectionTwo.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputTwo);
                locationImages.locationTwo = bitmap;

                imageThreeURL = new URL(thirdPrefix + "original" + thirdSuffix);
                connectionThree = (HttpURLConnection) imageThreeURL.openConnection();
                connectionThree.setDoInput(true);
                connectionThree.connect();
                inputThree = connectionThree.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputThree);
                locationImages.locationThree = bitmap;

            } else if (photos.length() > 1) {
                String firstPrefix = photos.getJSONObject(0).get("prefix").toString();
                String firstSuffix = photos.getJSONObject(0).get("suffix").toString();

                String secondPrefix = photos.getJSONObject(1).get("prefix").toString();
                String secondSuffix = photos.getJSONObject(1).get("suffix").toString();

                imageOneURL = new URL(firstPrefix + "original" + firstSuffix);
                connectionOne = (HttpURLConnection) imageOneURL.openConnection();
                connectionOne.setDoInput(true);
                connectionOne.connect();
                inputOne = connectionOne.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputOne);
                locationImages.locationOne = bitmap;

                imageTwoURL = new URL(secondPrefix + "original" + secondSuffix);
                connectionTwo = (HttpURLConnection) imageTwoURL.openConnection();
                connectionTwo.setDoInput(true);
                connectionTwo.connect();
                inputTwo = connectionTwo.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputTwo);
                locationImages.locationTwo = bitmap;
            } else if (photos.length() > 0) {
                String firstPrefix = photos.getJSONObject(0).get("prefix").toString();
                String firstSuffix = photos.getJSONObject(0).get("suffix").toString();

                imageOneURL = new URL(firstPrefix + "original" + firstSuffix);
                connectionOne = (HttpURLConnection) imageOneURL.openConnection();
                connectionOne.setDoInput(true);
                connectionOne.connect();
                inputOne = connectionOne.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputOne);
                locationImages.locationOne = bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locationImages;
    }

    public void clearPassportGlobalVals() {
        GlobalVars.adventourLocationsPassport.clear();
        GlobalVars.beaconModelArrayListPassport.clear();
        GlobalVars.adventourFSQIdsPassport.clear();
        GlobalVars.selectedLocationPassport = "";
    }
}