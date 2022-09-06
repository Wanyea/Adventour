package com.example.adventourandroidversion;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignupScreen extends AppCompatActivity {

    final Calendar birthdateCalendar = Calendar.getInstance();
    String username, email, birthdate, password;
    Button signupButton;
    TextView loginTextView;
    EditText birthdateDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        loginTextView = (TextView) findViewById(R.id.loginTextView);
        signupButton = (Button) findViewById(R.id.signupButton);
        birthdateDatePicker = (EditText) findViewById(R.id.birthdateEditText);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                birthdateCalendar.set(Calendar.YEAR, year);
                birthdateCalendar.set(Calendar.MONTH, month);
                birthdateCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        birthdateDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), date,
                        birthdateCalendar.get(Calendar.YEAR),
                        birthdateCalendar.get(Calendar.MONTH),
                        birthdateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               Intent intent = new Intent(v.getContext(), LoginScreen.class);
               startActivity(intent);
           }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = findViewById(R.id.usernameEditText).toString();
                email = findViewById(R.id.emailEditText).toString();
                birthdate = findViewById(R.id.birthdateEditText).toString(); //FIX THIS AFTER ADD DATEPICKER FEATURE
                password = findViewById(R.id.passwordEditText).toString();

                // Create new document in Firebase with the user information.
                // If successful, go to Home intent.
            }
        });
    }

    private void updateLabel(){
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        birthdateDatePicker.setText(dateFormat.format(birthdateCalendar.getTime()));
    }
}