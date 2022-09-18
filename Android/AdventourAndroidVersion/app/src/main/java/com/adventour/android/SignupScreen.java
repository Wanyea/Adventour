package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignupScreen extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    final Calendar birthdateCalendar = Calendar.getInstance();
    String nickname, email, birthdate, password, confirmPassword;
    Button signupButton;
    TextView loginTextView;
    EditText birthdateDatePicker;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, StartAdventour.class);
            startActivity(intent);
            finish();
        }

        mAuth = FirebaseAuth.getInstance();

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
                nickname = ((EditText) findViewById(R.id.nicknameEditText)).getText().toString().trim();
                email = ((EditText) findViewById(R.id.emailEditText)).getText().toString().trim();
                birthdate = AdventourUtils.formatBirthdateForDB(birthdateCalendar);
                password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString().trim();
                confirmPassword = ((EditText) findViewById(R.id.confirmPasswordEditText)).getText().toString().trim();

                // Check passwords match
                if (!AdventourUtils.checkPasswordsMatch(password, confirmPassword))
                {
                    displayPasswordError();
                    return;
                }
                // Create new document in Firebase with the user information.
                signUp(nickname, email, password, birthdate);


                // If successful, go to Home intent.

            }
        });
    }

    private void displayPasswordError() {
        Log.d("PasswordsDoNotMatch", "Passwords must match to sign up.");
    }

    private void displaySignUpError() {
        Log.d("BadSignUp", "There was a problem with sign up, please try again later.");
    }

    private void updateLabel(){
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        birthdateDatePicker.setText("Birthdate - " + dateFormat.format(birthdateCalendar.getTime()));
    }

    private void addUserToFirestore(String nickname, String email, String birthdate) {

        Map<String, Object> adventourist = new HashMap<>();
        adventourist.put("nickname", nickname);
        adventourist.put("email", email);
        adventourist.put("birthdate", birthdate);
        adventourist.put("isPrivate", true);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Adventourists").document(user.getUid())
                .set(adventourist)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    private void signUp(String nickname, String email, String password, String birthdate) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            addUserToFirestore(nickname, email, birthdate);
                            switchToHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupScreen.this, "Account creation failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void switchToHome() {
        Intent intent = new Intent(this, StartAdventour.class);
        startActivity(intent);
        finish();
    }
}