package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginScreen extends AppCompatActivity {

    EditText nicknameEditText, passwordEditText;
    TextView signupTextView;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        nicknameEditText = (EditText) findViewById(R.id.nicknameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupTextView = (TextView) findViewById(R.id.signupTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nickname = nicknameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                //Make a call to Firebase to store this information in the appropriate collection.
                // If successful, go to Home intent.
                startActivity(new Intent(v.getContext(), Home.class)); // TESTING ONLY
            }
        });

        signupTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignupScreen.class);
                startActivity(intent);
            }
        });

    }
}