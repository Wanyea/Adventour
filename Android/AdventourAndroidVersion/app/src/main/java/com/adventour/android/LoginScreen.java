package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreen extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText emailEditText, passwordEditText, forgotPasswordEmailEditText;
    TextView signupTextView, emailLoginErrorTextView, passwordLoginErrorTextView, resetPasswordTextView, exitButton;
    Button loginButton, sendButton;
    FirebaseAuth mAuth;
    ImageView emailLoginErrorIcon, passwordLoginErrorIcon;
    CardView resetPasswordPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Check for user logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
        {
            Intent intent = new Intent(this, StartAdventour.class);
            startActivity(intent);
            finish();
        }

        mAuth = FirebaseAuth.getInstance();

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupTextView = (TextView) findViewById(R.id.signupTextButton);
        resetPasswordTextView = (TextView) findViewById(R.id.resetPasswordTextButton);
        resetPasswordPopup = (CardView) findViewById(R.id.popupFilter);
        resetPasswordPopup.setVisibility(View.INVISIBLE);
        exitButton = (TextView) findViewById(R.id.exitButton);
        sendButton = (Button) findViewById(R.id.sendButton);
        forgotPasswordEmailEditText = (EditText) findViewById(R.id.forgotPasswordEmailEditText);

        emailLoginErrorTextView = (TextView) findViewById(R.id.emailLoginErrorTextView);
        passwordLoginErrorTextView = (TextView) findViewById(R.id.passwordLoginErrorTextView);
        emailLoginErrorIcon = (ImageView) findViewById(R.id.emailLoginErrorIcon);
        passwordLoginErrorIcon = (ImageView) findViewById(R.id.passwordLoginErrorIcon);

        
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Handle nulls before attempting to check for user in database
                if (AdventourUtils.isEmailEmpty(email))
                    displayNullEmailError();

                if (AdventourUtils.isPasswordEmpty(password))
                    displayNullPasswordError();

                if (!AdventourUtils.isEmailEmpty(email))
                    noEmptyEmailError();

                if (!AdventourUtils.isPasswordEmpty(password))
                    noEmptyPasswordError();

                if (!AdventourUtils.isEmailEmpty(email) && !AdventourUtils.isPasswordEmpty(password))
                    logIn(email, password);
            }
        });


        signupTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchToSignUp();
            }
        });

        resetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onResetPasswordClick(v);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPasswordPopup.setVisibility(View.INVISIBLE);
                emailEditText.setVisibility(View.VISIBLE);
                passwordEditText.setVisibility(View.VISIBLE);
                signupTextView.setVisibility(View.VISIBLE);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = forgotPasswordEmailEditText.getText().toString().trim();

                if (AdventourUtils.isEmailEmpty(email))
                    displayNullEmailError();
                else
                    sendResetEmail(email);
            }
        });
    }

    private void switchToSignUp() {
        Intent intent = new Intent(this, SignupScreen.class);
        startActivity(intent);
        finish();
    }

    private void switchToStartAdventour() {
        Intent intent = new Intent(this, StartAdventour.class);
        startActivity(intent);
        finish();
    }

    private void logIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            noError();
                            switchToStartAdventour();
                            Log.d(TAG, "Login attempt SUCCESS!");
                        } else {
                            Log.d(TAG, "Login attempt failed!");
                            displayError();
                        }
                    }
                });
    }

    // Renders error message and icon
    private void displayError()
    {
        int red_variant = getResources().getColor(R.color.red_variant);
        emailLoginErrorTextView.setText("Invalid email and password combination");
        passwordLoginErrorTextView.setText("Invalid email and password combination");
        emailEditText.getBackground().setColorFilter(red_variant, PorterDuff.Mode.SRC_ATOP);
        passwordEditText.getBackground().setColorFilter(red_variant, PorterDuff.Mode.SRC_ATOP); ;
        emailLoginErrorTextView.setVisibility(View.VISIBLE);
        passwordLoginErrorTextView.setVisibility(View.VISIBLE);
        emailLoginErrorIcon.setVisibility(View.VISIBLE);
        passwordLoginErrorIcon.setVisibility(View.VISIBLE);
    }

    // Disables error message and icon
    private void noError()
    {
        int blue_main = getResources().getColor(R.color.blue_main);
        emailEditText.getBackground().setColorFilter(blue_main, PorterDuff.Mode.SRC_ATOP);
        passwordEditText.getBackground().setColorFilter(blue_main, PorterDuff.Mode.SRC_ATOP); ;
        emailLoginErrorTextView.setVisibility(View.INVISIBLE);
        passwordLoginErrorTextView.setVisibility(View.INVISIBLE);
        emailLoginErrorIcon.setVisibility(View.INVISIBLE);
        passwordLoginErrorIcon.setVisibility(View.INVISIBLE);
    }

    private void displayNullEmailError()
    {
        int red_variant = getResources().getColor(R.color.red_variant);
        emailLoginErrorTextView.setText("Email cannot be empty!");
        emailEditText.getBackground().setColorFilter(red_variant, PorterDuff.Mode.SRC_ATOP);
        emailLoginErrorTextView.setVisibility(View.VISIBLE);
        emailLoginErrorIcon.setVisibility(View.VISIBLE);
    }

    private void displayNullPasswordError()
    {
        int red_variant = getResources().getColor(R.color.red_variant);
        passwordLoginErrorTextView.setText("Password cannot be empty!");
        passwordEditText.getBackground().setColorFilter(red_variant, PorterDuff.Mode.SRC_ATOP);
        passwordLoginErrorTextView.setVisibility(View.VISIBLE);
        passwordLoginErrorIcon.setVisibility(View.VISIBLE);
    }

    private void noEmptyEmailError() {
        int blue_main = getResources().getColor(R.color.blue_main);
        emailEditText.getBackground().setColorFilter(blue_main, PorterDuff.Mode.SRC_ATOP);
        emailLoginErrorTextView.setVisibility(View.INVISIBLE);
        emailLoginErrorIcon.setVisibility(View.INVISIBLE);
    }

    private void noEmptyPasswordError() {
        int blue_main = getResources().getColor(R.color.blue_main);
        passwordEditText.getBackground().setColorFilter(blue_main, PorterDuff.Mode.SRC_ATOP);
        passwordLoginErrorTextView.setVisibility(View.INVISIBLE);
        passwordLoginErrorIcon.setVisibility(View.INVISIBLE);
    }

    private void onResetPasswordClick(View v) {
        resetPasswordPopup.setVisibility(View.VISIBLE);
        emailEditText.setVisibility(View.INVISIBLE);
        passwordEditText.setVisibility(View.INVISIBLE);
        signupTextView.setVisibility(View.INVISIBLE);
    }

    private void sendResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Password reset sent");
                } else {
                    Log.d(TAG, "Password reset failed to send");
                }
            }
        });
    }
}