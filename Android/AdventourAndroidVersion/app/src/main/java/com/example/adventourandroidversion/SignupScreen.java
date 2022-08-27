package com.example.adventourandroidversion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignupScreen extends AppCompatActivity {

    String usernameEditText, emailEditText, birthdateEditText, passwordEditText;
    Button signupButton;
    TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        loginTextView = (TextView) findViewById(R.id.loginTextView);
        signupButton = (Button) findViewById(R.id.signupButton);

        loginTextView.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               Intent intent = new Intent(v.getContext(), LoginScreen.class);
               startActivity(intent);
           }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                usernameEditText = findViewById(R.id.usernameEditText).toString();
                emailEditText = findViewById(R.id.emailEditText).toString();
                birthdateEditText = findViewById(R.id.birthdateEditText).toString();
                passwordEditText = findViewById(R.id.passwordEditText).toString();

                // Create new document in Firebase with the user information.
                // If successful, go to Home intent.
            }
        });
    }


}