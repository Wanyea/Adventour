package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.Slider;



public class StartAdventour extends AppCompatActivity {

    ImageButton filterButton;
    Slider priceSlider;
    TextView priceTextView;
    Button inProgressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_adventour);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.start_adventour);

        // Filter button
        filterButton = (ImageButton) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFilterButton(view);
            }
        });

        inProgressButton = (Button) findViewById(R.id.inProgressButton);

        inProgressButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view)
           {
              switchToAdventourSummary();
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

    // Testing only, delete before prod
    public void switchToAdventourSummary() {
        Intent intent = new Intent(this, AdventourSummary.class);
        startActivity(intent);
        finish();
    }


}
