package com.adventour.android;

//import java.io.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
//import com.example.Fragment.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;
import android.view.MenuItem;

public class Home extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.start_adventour);
    }
    Account account = new Account();
    Beacons beacons = new Beacons();
    StartAdventour startAdventour = new StartAdventour();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.account:
                getSupportFragmentManager().beginTransaction().replace(R.id.home, account).commit();
                return true;

            case R.id.beacons:
                getSupportFragmentManager().beginTransaction().replace(R.id.home, beacons).commit();
                return true;

            case R.id.start_adventour:
                getSupportFragmentManager().beginTransaction().replace(R.id.home, startAdventour).commit();
                return true;
        }
        return false;
    }
}