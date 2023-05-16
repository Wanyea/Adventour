package com.adventour.android;

import static com.adventour.android.BuildConfig.MAPS_API_KEY;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.Slider;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class StartAdventour extends AppCompatActivity {

    PlacesClient placesClient;

    ImageButton filterButton;
    Slider distanceSlider;
    TextView nameTextView, distanceTextView, phoneTextView, websiteTextView, descriptionTextView, noLocationTextView, twentyOneTextView;
    Button goButton, doneButton, notNowButton, yesButton;
    RatingBar ratingBar;
    ImageView phoneImageView, globeImageView, previewImageView;
    ProgressBar progressBar;
    ScrollView scrollView;

    FirebaseAuth auth;
    FirebaseUser user;

    Switch socialSwitch, outdoorsySwitch, culturedSwitch, romanticSwitch, geekySwitch, spiritualSwitch,
            sportySwitch, chillSwitch, shoppableSwitch, pamperedSwitch, courageousSwitch, starvingSwitch, snackSwitch, twentyOnePlusSwitch;

    CardView popupFilter;

    HashMap<String, String> isSwitchActive;
    ArrayList<String> prevLocation = new ArrayList<>();

    int distance = 1;
    String currentFSQId;

    JSONObject jsonBody = new JSONObject();
    String name, description, tel, website, address, userLocLat, userLocLng;
    Double lat, lon;
    float rating;
    LocationImages locationImages = new LocationImages();
    AutocompleteSupportFragment autocompleteFragment;

    HashMap<String, Integer> drawableIds = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_adventour);

        // Dump Global Vars
        GlobalVars.userBeaconsArrayList.clear();
        GlobalVars.previousAdventourArrayList.clear();
        GlobalVars.beaconBoardArrayList.clear();

        drawableIds.put("Cheetah", 0);
        drawableIds.put("Elephant", 1);
        drawableIds.put("Ladybug", 2);
        drawableIds.put("Fox", 4);
        drawableIds.put("Penguin", 5);
        drawableIds.put("Monkey", 3);

        Log.d("Drawable Ids: ", drawableIds.toString());

        // This allows the API call on the main thread.
        // Moving forward we are going to want to put this on its own thread to increase overall app performance :)

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        handleAuth();
        getUser();

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.start_adventour);

        filterButton = (ImageButton) findViewById(R.id.filterButton);
        goButton = (Button) findViewById(R.id.goButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        notNowButton = (Button) findViewById(R.id.notNowButton);
        yesButton = (Button) findViewById(R.id.yesButton);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneNumberTextView);
        websiteTextView = (TextView) findViewById(R.id.websiteTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionEditText);
        noLocationTextView = (TextView) findViewById(R.id.noLocationsTextView);
        twentyOneTextView = (TextView) findViewById(R.id.twentyOneTextView);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        popupFilter = (CardView) findViewById(R.id.popupFilter);
        popupFilter.setVisibility(View.INVISIBLE);

        socialSwitch = findViewById(R.id.socialSwitch);
        outdoorsySwitch = findViewById(R.id.outdoorsySwitch);
        culturedSwitch = findViewById(R.id.culturedSwitch);
        romanticSwitch = findViewById(R.id.romanticSwitch);
        geekySwitch = findViewById(R.id.geekySwitch);
        spiritualSwitch = findViewById(R.id.spiritualSwitch);
        sportySwitch = findViewById(R.id.sportySwitch);
        chillSwitch = findViewById(R.id.chillSwitch);
        shoppableSwitch = findViewById(R.id.shoppableSwitch);
        pamperedSwitch = findViewById(R.id.pamperedSwitch);
        courageousSwitch = findViewById(R.id.courageousSwitch);
        starvingSwitch = findViewById(R.id.starvingSwitch);
        snackSwitch = findViewById(R.id.snackSwitch);
        twentyOnePlusSwitch = findViewById(R.id.twentyOnePlusSwitch);

        phoneImageView = (ImageView) findViewById(R.id.phoneImageView);
        globeImageView = (ImageView) findViewById(R.id.globeImageView);
        previewImageView = (ImageView) findViewById(R.id.previewImageView);

        distanceSlider = findViewById(R.id.distanceSlider);

        progressBar = findViewById(R.id.progressBar);

        websiteTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        isSwitchActive = new HashMap<String, String>();

        if (getIntent().getSerializableExtra("isSwitchActive") != null)
        {
            isSwitchActive = (HashMap) getIntent().getSerializableExtra("isSwitchActive");
        }

        if (getIntent().getSerializableExtra("distance") != null)
        {
            distance = (int) getIntent().getSerializableExtra("distance");
            distanceSlider.setValue(distance);
        }

        if (prevLocation.size() > 0)
        {
            nameTextView.setText(prevLocation.get(0));
        } else {
            nameTextView.setText("Click GO Button to begin!");
        }

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onClickFilterButton(view);
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               scrollView.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                   }
               },1000);

               setLocationVisibility(false);
               progressBar.setVisibility(View.VISIBLE);

               jsonBody = new JSONObject();
               getLocation();
           }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               if (isLocationSelected() && isTagSelected())
                   goButton.setEnabled(true);

               popupFilter.setVisibility(View.INVISIBLE);
           }
        });

        notNowButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               setLocationVisibility(false);
               progressBar.setVisibility(View.VISIBLE);

               GlobalVars.excludes.put(currentFSQId);
               jsonBody = new JSONObject();
               getLocation();
           }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               GlobalVars.excludes.put(currentFSQId);
               GlobalVars.adventourFSQIds.add(currentFSQId);
               GlobalVars.adventourLocations.add(new AdventourSummaryModel(name, description, locationImages.locationOne));
               GlobalVars.inProgressModelArrayList.add(new InProgressModel(name, lat, lon));
               GlobalVars.beaconModelArrayList.add(new BeaconPostModel(name, rating, address, description, locationImages));
               switchToInProgress();
           }
        });

        distanceSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider distanceSlider, float value, boolean fromUser) {
                distance = (int) value;
            }
        });

        // Initialize places client
        Places.initialize(this, MAPS_API_KEY);
        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setCountry("US");
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.OVERLAY);
        autocompleteFragment.setHint("Enter location");
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);

        if (!Objects.equals(GlobalVars.selectedLocation, ""))
        {
            Log.d("locationName, SL is empty", GlobalVars.lastLocationName);

            autocompleteFragment.setText(GlobalVars.lastLocationName);
        }

        ((EditText) autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)).setTextSize(18.0f);
        ((EditText) autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)).setHintTextColor(Color.BLACK);

        autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        autocompleteFragment.setText("");
                        view.setVisibility(View.GONE);
                    }
                });

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                GlobalVars.selectedLocation = String.valueOf(place.getAddress());
                GlobalVars.selectedLocationID = place.getId();
                GlobalVars.locationCoordinates = place.getLatLng();
                GlobalVars.lastLocationName = place.getName();

                Log.d("locationName", GlobalVars.lastLocationName);

                if(isTagSelected())
                    goButton.setEnabled(true);

                Log.i("Start Adventour", "Place: " + GlobalVars.selectedLocation + ", " + GlobalVars.selectedLocationID + ", " + GlobalVars.locationCoordinates);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("Start Adventour", "An error occurred: " + status);
            }
        });


        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnItemSelectedListener) item -> {

            switch(item.getItemId())
            {
                case R.id.passport:
                    Intent passportIntent = new Intent(getApplicationContext(), Passport.class);
                    passportIntent.putExtra("isSwitchActive", isSwitchActive);
                    passportIntent.putExtra("distance", distance);
                    startActivity(passportIntent);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.start_adventour:
                    return true;
                case R.id.beacons:
                    Intent beaconsIntent = new Intent(getApplicationContext(), Beacons.class);
                    beaconsIntent.putExtra("isSwitchActive", isSwitchActive);
                    beaconsIntent.putExtra("distance", distance);
                    startActivity(beaconsIntent);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

    }

    public void getLocation()
    {
        userLocLat = Double.toString(GlobalVars.locationCoordinates.latitude);
        userLocLng = Double.toString(GlobalVars.locationCoordinates.longitude);
        GlobalVars.lastLocationCoordinates = userLocLat + "," + userLocLng;

        try {

            jsonBody.put("uid", user.getUid());
            jsonBody.put("ll", GlobalVars.lastLocationCoordinates);
            jsonBody.put("radius", getDistance());
            jsonBody.put("categories", getCategoriesString());
            jsonBody.put("excludes", getExclude());

        } catch (JSONException e) {
            Log.e("Start Adventour", "exception", e);
        }

        try {

            // Call API with user defined location and sentiments
            URL url = new URL("https://adventour-183a0.uc.r.appspot.com/get-adventour-place");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty( "Content-Type", "application/json");
            conn.setRequestProperty( "Accept", "application/json");
            conn.setUseCaches( false );

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
            System.out.println(responseData); // Printing for dev testing

            try {
                JSONObject data = (JSONObject) responseData.get("data");
                currentFSQId = data.get("fsq_id").toString();
                name = data.get("name").toString();
                GlobalVars.lastLocationName = name;
                rating = Float.parseFloat(data.get("rating").toString()) / 2;

                Runnable phoneRun = new Runnable() {
                    public void run() {
                        try {
                            tel = data.get("tel").toString();
                            Log.d("Telephone", tel);
                        } catch(Exception e) {
                            tel = "N/A";
                            Log.d("Telephone", tel);
                            Log.e("No tel for location", "Exception", e);
                        }
                    }
                };

                Runnable websiteRun = new Runnable() {
                    public void run() {
                        try {
                            website = data.get("website").toString();
                            Log.d("Website", website);
                        } catch(Exception e) {
                            website = "N/A";
                            Log.d("Website", website);
                            Log.e("No web for location", "Exception", e);
                        }
                    }
                };

                Runnable descRun = new Runnable() {
                    public void run() {
                        try {
                            description = data.get("description").toString();
                        } catch(Exception e) {
                            description = "No description available for this location... ";
                            Log.e("No des for location", "Exception", e);
                        }
                    }
                };

                Runnable addressRun = new Runnable() {
                    public void run() {
                        try {
                            JSONObject location = (JSONObject) data.get("location");
                            address = location.get("formatted_address").toString();
                        } catch(Exception e) {
                            address = "No address available for this location... ";
                            Log.e("No address for location", "Exception", e);
                        }
                    }
                };

                Runnable photoRun = new Runnable() {
                    public void run() {
                        try {
                            JSONArray photos = (JSONArray) data.get("photos");
                            String prefix = photos.getJSONObject(0).get("prefix").toString();
                            String suffix = photos.getJSONObject(0).get("suffix").toString();

                            URL imageURL = new URL(prefix + "original" + suffix);
                            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            Bitmap myBitmap = BitmapFactory.decodeStream(input);
                            Log.e("Bitmap","returned");
                            previewImageView.setImageBitmap(myBitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                // Try to get the first image from the API response and pass it into the
                // LocationImages object.
                Runnable imageRun = new Runnable() {
                    public void run() {
                        try {
                            JSONArray photos = (JSONArray) data.get("photos");
                            URL imageOneURL, imageTwoURL, imageThreeURL;
                            HttpURLConnection connectionOne, connectionTwo, connectionThree;
                            InputStream inputOne, inputTwo, inputThree;
                            Bitmap bitmap;

                            // Try to get first location image.
                            if (photos.length() > 2)
                            {
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

                    }
                };

                Runnable newLatLongRun = new Runnable() {
                    public void run()
                    {
                        try {
                            if (data.get("geocodes") != null)
                            {
                                JSONObject geocodes = (JSONObject) data.get("geocodes");
                                Log.d("geocodes", geocodes.toString());

                                if (geocodes.get("main") != null)
                                {
                                    JSONObject main = (JSONObject) geocodes.get("main");
                                    Log.d("main", main.toString());

                                    if (geocodes.get("main") != null)
                                    {
                                        GlobalVars.lastLocationCoordinates = String.valueOf(main.get("latitude")) + "," + String.valueOf(main.get("longitude"));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                Thread t1 = new Thread(phoneRun);
                Thread t2 = new Thread(websiteRun);
                Thread t3 = new Thread(descRun);
                Thread t4 = new Thread(addressRun);
                Thread t5 = new Thread(photoRun);
                Thread t6 = new Thread(imageRun);
                Thread t7 = new Thread(newLatLongRun);


                t1.start();
                t2.start();
                t3.start();
                t4.start();
                t5.start();
                t6.start();
                t7.start();

                t1.join();
                t2.join();
                t3.join();
                t4.join();
                t5.join();
                t6.join();
                t7.join();

                populateCard(name, rating, tel, website, description);

                try {
                    JSONObject geocodes = (JSONObject) data.get("geocodes");
                    JSONObject main = (JSONObject) geocodes.get("main");

                    lat = (Double) main.get("latitude");
                    lon = (Double) main.get("longitude");
                } catch (Exception e) {
                    lat = 1000.0;
                    lon = 1000.0;
                }

            } catch (Exception e) {
                Log.e("NO LOC W CURR TAGS/LOC", "Exception", e);
                noLocationTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                nameTextView.setVisibility(View.INVISIBLE);
                phoneTextView.setVisibility(View.INVISIBLE);
                websiteTextView.setVisibility(View.INVISIBLE);
                descriptionTextView.setVisibility(View.INVISIBLE);
                ratingBar.setVisibility(View.INVISIBLE);
                phoneImageView.setVisibility(View.INVISIBLE);
                globeImageView.setVisibility(View.INVISIBLE);
                previewImageView.setVisibility(View.INVISIBLE);
                yesButton.setEnabled(false);
                notNowButton.setEnabled(false);
                setLocationVisibility(false);
            }

        } catch(Exception e) {
            Log.e("START ADVENTOUR", "Exception", e);

        }
    }

    public void populateCard(String name, float rating, String tel, String website, String description)
    {
        Context c = this;

        new Thread() {
            public void run() {
                runOnUiThread(() -> {
                    setLocationVisibility(true);
                    noLocationTextView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                    notNowButton.setEnabled(true);
                    yesButton.setEnabled(true);

                    nameTextView.setText(name);

                    if (tel != null)
                    {
                        phoneTextView.setText(tel);
                        Linkify.addLinks(phoneTextView, Linkify.PHONE_NUMBERS);
                    }

                    if (website != null)
                    {
                        websiteTextView.setText(website);

                        websiteTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(website));
                                startActivity(i);
                            }
                        });
                    }

                    descriptionTextView.setText(description);
                    ratingBar.setRating(rating);
                });
            }
        }.start();
    }

    public void getUser()
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
                        Log.d("START ADVENTOUR", "DocumentSnapshot data: " + document.getData());
                        Calendar userBirthdate = Calendar.getInstance();
                        userBirthdate.setTime(((Timestamp) document.get("birthdate")).toDate());
                        Log.d("calendar date", String.valueOf(userBirthdate.get(Calendar.DATE)));
                        Log.d("calendar month", String.valueOf(userBirthdate.get(Calendar.MONTH)));
                        Log.d("calendar year", String.valueOf(userBirthdate.get(Calendar.YEAR)));

                        // Remove "21+" tag if user is not at least 21.
                        if (!AdventourUtils.isUserOver21Plus(userBirthdate.get(Calendar.DATE), userBirthdate.get(Calendar.MONTH), userBirthdate.get(Calendar.YEAR)))
                        {
                            twentyOneTextView.setVisibility(View.INVISIBLE);
                            twentyOnePlusSwitch.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        Log.d("START ADVENTOUR", "No such document");
                    }
                } else {
                    Log.d("START ADVENTOUR", "get failed with ", task.getException());
                }
            }
        });
    }

    String getCategoriesString()
    {
        // Reset categories and rebuild arraylist before every API call.
        ArrayList<String> categories = new ArrayList<String>();

        if (socialSwitch.isChecked())
        {
           String socialString = "10001,10003,10006,10007,10009,10017,10019,10022,10023,10039,10048,10055,10056,12004,14003,14009,10015,";
           categories.add(socialString);
        }

        if (outdoorsySwitch.isChecked())
        {
            String outdoorsyString = "10014,10044,10055,10056,16002,16003,16004,16005,16006,16008,16009,16011,16012,16013,16016,16017,16018,16019,16021,16022,16023,16024,16027,16028,16032,16043,16044,16046,16048,16049,16051,16052,19002,19003,19008,19021,";

            categories.add(outdoorsyString);
        }

        if (culturedSwitch.isChecked())
        {
            String culturedString = "10002,10004,10016,10024,10028,10031,10030,10042,17002,17003,10043,17018,10047,10056,11005,17098,17113,11140,12005,12065,12066,12080,12081,16011,16024,16007,16020,16025,16026,16031,17103,16047,";

            categories.add(culturedString);
        }

        if (romanticSwitch.isChecked())
        {
            String romanticString = "10004,10016,10024,10023,11140,16005,11073,";

            categories.add(romanticString);
        }

        if (geekySwitch.isChecked())
        {
            String geekyString = "10003,10015,10018,17018,17022,17091,17027,17135,10044,10054,12080,12081,";

            categories.add(geekyString);
        }

        if (spiritualSwitch.isChecked())
        {
            String spiritualString = "12098,";

            categories.add(spiritualString);
        }

        if (sportySwitch.isChecked())
        {
            String sportyString = "10006,10014,10019,10022,10023,10045,10048,18005,18008,18012,18019,18020,18021,18029,18034,17117,18035,18036,18037,18039,18040,18048,18054,18057,18058,18064,18067,19002,";

            categories.add(sportyString);
        }

        if (chillSwitch.isChecked())
        {
            String chillString = "10003,10006,10015,10020,10024,10025,10045,10056,11005,11073,12080,12081,19021,16003,16005,16032,";

            categories.add(chillString);
        }

        if (shoppableSwitch.isChecked())
        {
            String shoppableString = "14009,17002,17002,17004,17018,17020,17022,17027,17024,17030,17031,17032,17039,17053,17054,17055,17056,17089,17091,17098,17107,17111,17113,17116,17117,17135,17138,17103,";

            categories.add(shoppableString);
        }

        if (pamperedSwitch.isChecked())
        {
            String pamperedString = "11062,11063,11064,11071,11072,11073,11074,11070,17030,11140,15001,17020,";

            categories.add(pamperedString);
        }

        if (courageousSwitch.isChecked())
        {
            String courageousString = "11065,11075,19008,18057,";

            categories.add(courageousString);
        }

        if (starvingSwitch.isChecked())
        {
            String starvingString = "13028,13053,13054,13065,";

            categories.add(starvingString);
        }

        if (snackSwitch.isChecked())
        {
            String snackString = "13001,13002,13032,13040,13059,13381,13382,";

            categories.add(snackString);
        }

        if (twentyOnePlusSwitch.isChecked())
        {
            String twentyOnePlusString = "10008,10010,10029,10032,10052,13003,13029,13038,13050,13061,13386,13387,16029,";

            categories.add(twentyOnePlusString);
        }

        return String.join("", categories);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean isTagSelected()
    {
        return isSwitchActive.size() != 0;
    }

    private boolean isLocationSelected()
    {
        return !((TextView) autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)).getText().toString().isEmpty();
    }

    public void onClickFilterButton(View view) {
        popupFilter.setVisibility(View.VISIBLE);

        if((isSwitchActive.get("social")) != null && isSwitchActive.get("social").equals("true"))
        {
            socialSwitch.setChecked(true);
        }

        if((isSwitchActive.get("outdoorsy")) != null && isSwitchActive.get("outdoorsy").equals("true"))
        {
            outdoorsySwitch.setChecked(true);
        }

        if((isSwitchActive.get("cultured")) != null && isSwitchActive.get("cultured").equals("true"))
        {
            culturedSwitch.setChecked(true);
        }

        if((isSwitchActive.get("romantic")) != null && isSwitchActive.get("romantic").equals("true"))
        {
            romanticSwitch.setChecked(true);
        }

        if((isSwitchActive.get("geeky")) != null && isSwitchActive.get("geeky").equals("true"))
        {
            geekySwitch.setChecked(true);
        }

        if((isSwitchActive.get("spiritual")) != null && isSwitchActive.get("spiritual").equals("true"))
        {
            spiritualSwitch.setChecked(true);
        }

        if((isSwitchActive.get("sporty")) != null && isSwitchActive.get("sporty").equals("true"))
        {
            sportySwitch.setChecked(true);
        }

        if((isSwitchActive.get("chill")) != null && isSwitchActive.get("chill").equals("true"))
        {
            chillSwitch.setChecked(true);
        }

        if((isSwitchActive.get("shoppable")) != null && isSwitchActive.get("shoppable").equals("true"))
        {
            shoppableSwitch.setChecked(true);
        }

        if((isSwitchActive.get("pampered")) != null && isSwitchActive.get("pampered").equals("true"))
        {
            pamperedSwitch.setChecked(true);
        }

        if((isSwitchActive.get("courageous")) != null && isSwitchActive.get("courageous").equals("true"))
        {
            courageousSwitch.setChecked(true);
        }

        if((isSwitchActive.get("starving")) != null && isSwitchActive.get("starving").equals("true"))
        {
            starvingSwitch.setChecked(true);
        }

        if((isSwitchActive.get("snack")) != null && isSwitchActive.get("snack").equals("true"))
        {
            snackSwitch.setChecked(true);
        }

        if((isSwitchActive.get("21+")) != null && isSwitchActive.get("21+").equals("true"))
        {
            twentyOnePlusSwitch.setChecked(true);
        }

        socialSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("social", "true");

                } else {
                    isSwitchActive.put("social", "false");
                }
            }
        });

        outdoorsySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("outdoorsy", "true");

                } else {
                    isSwitchActive.put("outdoorsy", "false");
                }
            }
        });

        culturedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("cultured", "true");

                } else {
                    isSwitchActive.put("cultured", "false");
                }
            }
        });

        romanticSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("romantic", "true");

                } else {
                    isSwitchActive.put("romantic", "false");
                }
            }
        });

        geekySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("geeky", "true");

                } else {
                    isSwitchActive.put("geeky", "false");
                }
            }
        });

        spiritualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("spiritual", "true");

                } else {
                    isSwitchActive.put("spiritual", "false");
                }
            }
        });

        chillSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("chill", "true");

                } else {
                    isSwitchActive.put("chill", "false");
                }
            }
        });

        sportySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("sporty", "true");

                } else {
                    isSwitchActive.put("sporty", "false");
                }
            }
        });

        chillSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("chill", "true");

                } else {
                    isSwitchActive.put("chill", "false");
                }
            }
        });

        shoppableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("shoppable", "true");

                } else {
                    isSwitchActive.put("shoppable", "false");
                }
            }
        });

        pamperedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("pampered", "true");

                } else {
                    isSwitchActive.put("pampered", "false");
                }
            }
        });

        courageousSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("courageous", "true");

                } else {
                    isSwitchActive.put("courageous", "false");
                }
            }
        });

        starvingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("starving", "true");

                } else {
                    isSwitchActive.put("starving", "false");
                }
            }
        });

        snackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("snack", "true");

                } else {
                    isSwitchActive.put("snack", "false");
                }
            }
        });

        twentyOnePlusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSwitchActive.put("21+", "true");

                } else {
                    isSwitchActive.put("21+", "false");
                }
            }
        });
    }

    public Integer getDistance()
    {
        return (int)(distance * 1609.344); // Miles --> Meters
    }

    public JSONArray getExclude()
    {
        return GlobalVars.excludes;
    }

    public void switchToInProgress()
    {
        Intent intent = new Intent(this, InProgress.class);
        intent.putExtra("isSwitchActive", isSwitchActive);
        intent.putExtra("distance", distance);
        Log.d("switchTOIP", isSwitchActive.toString());
        Log.d("switchTOIP", String.valueOf(distance));
        startActivity(intent);
        finish();
    }

    public void handleAuth()
    {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Log.d("AUTH TEST", user.getUid());

        if (user == null)
        {
            switchToLoggedOut();
        }
    }

    public void switchToLoggedOut()
    {
        Intent intent = new Intent(this, LoggedOut.class);
        startActivity(intent);
        finish();
    }

    public void setLocationVisibility(boolean visible) {
        if (visible) {
            nameTextView.setVisibility(View.VISIBLE);
            phoneTextView.setVisibility(View.VISIBLE);
            websiteTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            phoneImageView.setVisibility(View.VISIBLE);
            globeImageView.setVisibility(View.VISIBLE);
            previewImageView.setVisibility(View.VISIBLE);
        } else {
            nameTextView.setVisibility(View.INVISIBLE);
            phoneTextView.setVisibility(View.INVISIBLE);
            websiteTextView.setVisibility(View.INVISIBLE);
            descriptionTextView.setVisibility(View.INVISIBLE);
            ratingBar.setVisibility(View.INVISIBLE);
            phoneImageView.setVisibility(View.INVISIBLE);
            globeImageView.setVisibility(View.INVISIBLE);
            previewImageView.setVisibility(View.INVISIBLE);
        }
    }
}

