package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
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

public class StartAdventour extends AppCompatActivity {

    ImageButton filterButton;
    Slider priceSlider;
    TextView nameTextView, distanceTextView, phoneTextView, websiteTextView, descriptionTextView;
    Button inProgressButton, beginButton;
    RatingBar ratingBar;

    FirebaseAuth auth;
    FirebaseUser user;

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
        inProgressButton = (Button) findViewById(R.id.inProgressButton);
        beginButton = (Button) findViewById(R.id.beginButton);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneNumberTextView);
        websiteTextView = (TextView) findViewById(R.id.websiteTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onClickFilterButton(view);
            }
        });

        inProgressButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view)
           {
              switchToInProgress();
           }
        });

        beginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               getLocation();
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
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View filterPopupWindowView = inflater.inflate(R.layout.popup_filter, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow filterPopupWindow = new PopupWindow(filterPopupWindowView, width, height, focusable);
        filterPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        filterPopupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                filterPopupWindow.dismiss();
                return true;
            }
        });

        /*
        // Filter Price slider
        priceSlider = (Slider) findViewById(R.id.priceSlider);
        priceSlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
                currencyFormat.setCurrency(Currency.getInstance("USD"));
                return currencyFormat.format(value);
            }
        });*/

        // Filter Price Text View

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

    public void getLocation()
    {
        JSONObject jsonBody = new JSONObject();
        String fsqId, name, description, tel, website;
        float rating;

        try {
            jsonBody.put("uid", user.getUid());
            jsonBody.put("ll", "28.592474256389895,-81.3500389284532");
            jsonBody.put("radius", "10000");
            jsonBody.put("categories", "10014,10044,10055,10056,16002,16003,16004,16005,16006,16008,16009,16011,16012,16013,16016,16017,16019,16018,16021,16022,16023,16024,16027,16028,16032,16043,16044,16046,16048,16049,16051,16052,19002,19003,19008,19021");

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
                fsqId = data.get("fsq_id").toString();
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

                Log.d("START ADVENTOUR", fsqId + " " + name + " " + rating + " " + tel + " " + website + " " + description);
                populateCard(name, rating, tel, website, description);

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

}
