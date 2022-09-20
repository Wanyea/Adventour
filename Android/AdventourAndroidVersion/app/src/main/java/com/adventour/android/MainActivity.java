package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView loginTextView;
    TextView signupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setTheme(R.style.Theme_AdventourAndroidVersion);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, StartAdventour.class);
            startActivity(intent);
            finish();
        }

        signupTextView = (TextView) findViewById(R.id.signupTextView);
        loginTextView = (TextView) findViewById(R.id.loginTextView);


        loginTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchToLogin();
            }
        });

        signupTextView.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               switchToSignUp();
           }
        });
    }

    private void switchToLogin() {
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
        finish();
    }

    private void switchToSignUp() {
        Intent intent = new Intent(this, SignupScreen.class);
        startActivity(intent);
        finish();
    }
}