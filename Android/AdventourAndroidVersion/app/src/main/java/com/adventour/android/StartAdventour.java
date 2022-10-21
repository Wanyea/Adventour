package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StartAdventour extends AppCompatActivity {

    ImageButton filterButton;
    Slider distanceSlider;
    TextView nameTextView, distanceTextView, phoneTextView, websiteTextView, descriptionTextView;
    Button beginButton, doneButton, notNowButton, yesButton;
    RatingBar ratingBar;

    FirebaseAuth auth;
    FirebaseUser user;

    Switch socialSwitch, outdoorsySwitch, culturedSwitch, romanticSwitch, geekySwitch, spiritualSwitch,
            sportySwitch, chillSwitch, shoppableSwitch, pamperedSwitch, courageousSwitch, starvingSwitch, snackSwitch, twentyOnePlusSwitch;

    CardView popupFilter;

    HashMap<String, String> isSwitchActive;


    Integer distance = 0;
    String currentFSQId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_adventour);

        // This allows the API call on the main thread.
        // Moving forward we are going to want to put this on its own thread to increase overall app performance :)

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        handleAuth();

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.start_adventour);

        filterButton = (ImageButton) findViewById(R.id.filterButton);
        beginButton = (Button) findViewById(R.id.beginButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        notNowButton = (Button) findViewById(R.id.notNowButton);
        yesButton = (Button) findViewById(R.id.yesButton);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneNumberTextView);
        websiteTextView = (TextView) findViewById(R.id.websiteTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);

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
        twentyOnePlusSwitch = findViewById(R.id.twentyonePlusSwitch);

        distanceSlider = findViewById(R.id.distanceSlider);

        isSwitchActive = new HashMap<String, String>();


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onClickFilterButton(view);
            }
        });

        beginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               getLocation();
           }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               popupFilter.setVisibility(View.INVISIBLE);
           }
        });

        notNowButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               GlobalVars.exclude.add(currentFSQId);
               getLocation();
               if (GlobalVars.inProgressModelArrayList.size() > 0) { GlobalVars.inProgressModelArrayList.remove(0); }
               Log.d("EXCLUDE: ", String.join(",", GlobalVars.exclude));
           }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               GlobalVars.adventourFSQIds.add(currentFSQId);
               switchToInProgress();
           }
        });

        distanceSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider distanceSlider, float value, boolean fromUser) {
                distance = (int) value;
            }
        });

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnItemSelectedListener) item -> {

            switch(item.getItemId())
            {
                case R.id.passport:
                    startActivity(new Intent(getApplicationContext(), Passport.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.start_adventour:
                    return true;
                case R.id.beacons:
                    startActivity(new Intent(getApplicationContext(), Beacons.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

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


//        // Filter Price slider
//        priceSlider = (Slider) findViewById(R.id.priceSlider);
//        priceSlider.setLabelFormatter(new LabelFormatter() {
//            @NonNull
//            @Override
//            public String getFormattedValue(float value) {
//                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
//                currencyFormat.setCurrency(Currency.getInstance("USD"));
//                return currencyFormat.format(value);
//            }
//        });

        // Filter Price Text View

    }

    public void getLocation()
    {
        JSONObject jsonBody = new JSONObject();
        String fsqId, name, description, tel, website;
        float rating;

        try {

            jsonBody.put("uid", user.getUid());
            jsonBody.put("ll", "28.592474256389895,-81.3500389284532");
            jsonBody.put("radius", getDistance());
            jsonBody.put("categories", getCategoriesString());
            //jsonBody.put("exclude", getExclude());

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

            try {
                JSONObject data = (JSONObject) responseData.get("data");
                currentFSQId = data.get("fsq_id").toString();
                name = data.get("name").toString();
                rating = Float.parseFloat(data.get("rating").toString()) / 2;

                try {
                    tel = data.get("tel").toString();
                } catch(Exception e) {
                    tel = "N/A";
                    Log.e("No tel for location", "Exception", e);
                }

                try {
                    website = data.get("website").toString();
                } catch(Exception e) {
                    website = "N/A";
                    Log.e("No web for location", "Exception", e);
                }

                try {
                    description = data.get("description").toString();
                } catch(Exception e) {
                    description = "No description available for this location... ";
                    Log.e("No des for location", "Exception", e);
                }

                Log.d("START ADVENTOUR", currentFSQId + " " + name + " " + rating + " " + tel + " " + website + " " + description);
                populateCard(name, rating, tel, website, description);

                GlobalVars.inProgressModelArrayList.add(new InProgressModel(name, 28.602427, -81.200058)); // TODO: use real lat/long once Places API is working.

            } catch (Exception e) {
                Log.e("START ADVENTOUR", "Exception", e);
            }

        } catch(Exception e) {
            Log.e("START ADVENTOUR", "Exception", e);
        }
    }

    public void populateCard(String name, float rating, String tel, String website, String description)
    {
        nameTextView.setText(name);
        phoneTextView.setText(tel);
        websiteTextView.setText(website);
        descriptionTextView.setText(description);
        ratingBar.setRating(rating);
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

    public Integer getDistance()
    {
        return (int)(distance * 1609.344); // Miles --> Meters
    }

    public String getExclude()
    {
        return String.join(",", GlobalVars.exclude);
    }

    public void switchToInProgress()
    {
        Intent intent = new Intent(this, InProgress.class);
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

    public void switchToLoggedOut()
    {
        Intent intent = new Intent(this, LoggedOut.class);
        startActivity(intent);
        finish();
    }

}

