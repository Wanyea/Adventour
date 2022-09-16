package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

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
    TextView loginTextView, nicknameErrorTextView, emailErrorTextView, birthdateErrorTextView, passwordErrorTextView, confirmPasswordErrorTextView;
    ImageView nicknameErrorImageView, emailErrorImageView, birthdateErrorImageView, passwordErrorImageView, confirmPasswordErrorImageView;
    EditText birthdateDatePicker;
    int day, month, year;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        nicknameErrorTextView = (TextView) findViewById(R.id.nicknameSignupErrorTextView);
        emailErrorTextView = (TextView) findViewById(R.id.emailSignupErrorTextView);
        birthdateErrorTextView = (TextView) findViewById(R.id.birthdateSignupErrorTextView);
        passwordErrorTextView = (TextView) findViewById(R.id.passwordSignupErrorTextView);
        confirmPasswordErrorTextView = (TextView) findViewById(R.id.confirmPasswordSignupErrorTextView);

        nicknameErrorImageView = (ImageView) findViewById(R.id.nicknameSignupErrorIcon);
        emailErrorImageView = (ImageView) findViewById(R.id.emailSignupErrorIcon);
        birthdateErrorImageView = (ImageView) findViewById(R.id.birthdateSignupErrorIcon);
        passwordErrorImageView = (ImageView) findViewById(R.id.passwordLoginErrorIcon);
        confirmPasswordErrorImageView = (ImageView) findViewById(R.id.confirmPasswordSignupErrorIcon);


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

                // Parse birthdate
                String myFormat = "MM/dd/yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                String date = dateFormat.format(birthdateCalendar.getTime());
                day = Integer.parseInt(date.substring(0, 2));
                month = Integer.parseInt(date.substring(3, 5));
                year = Integer.parseInt(date.substring(6, 10));
                
                //Check if nickname is valid.
                if (!AdventourUtils.isValidNickname(nickname))
                {
                    displayNicknameError();
                } else {
                    noNicknameError();
                }

                // Check if email address is valid.
                if (!AdventourUtils.isValidEmail(email))
                {
                    displayEmailError();
                } else {
                    noEmailError();
                }

                // Check if user is at least 13 years old.
                if (!AdventourUtils.isUserOver13(day, month, year))
                {
                    displayBirthdateError();
                } else {
                   noBirthdateError();
                }
                // Check passwords match
                if (!AdventourUtils.checkPasswordsMatch(password, confirmPassword))
                {
                    displayPasswordError();
                } else {
                    noPasswordError();
                }
                // Create new document in Firebase with the user information.
                //signUp(nickname, email, password, birthdate);


                // If successful, go to Home intent.
                // Finish SignUpScreen Intent.

            }
        });
    }

    private void displayNicknameError() {
        Log.d("NicknameInvalid", "Nickname must be between 6 and 20 characters.");
        // Render error message and icon
        nicknameErrorTextView.setVisibility(View.VISIBLE);
        nicknameErrorImageView.setVisibility(View.VISIBLE);
    }

    private void displayEmailError() {
        Log.d("EmailInvalid", "Invalid email.");
        //Render error message and icon
        emailErrorTextView.setVisibility(View.VISIBLE);
        emailErrorImageView.setVisibility(View.VISIBLE);
    }

    private void displayBirthdateError() {
        Log.d("BirthdateInvalid", "User is not 13 years of age.");
        //Render error message and icon
        birthdateErrorTextView.setVisibility(View.VISIBLE);
        birthdateErrorImageView.setVisibility(View.VISIBLE);

    }
    private void displayPasswordError() {
        Log.d("PasswordsDoNotMatch", "Passwords must match to sign up.");
        //Render error message and icon
        passwordErrorTextView.setVisibility(View.VISIBLE);
        passwordErrorImageView.setVisibility(View.VISIBLE);

        confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
        confirmPasswordErrorImageView.setVisibility(View.VISIBLE);
    }


    private void displaySignUpError() {
        Log.d("BadSignUp", "There was a problem with sign up, please try again later.");
    }

    private void noNicknameError()
    {
        nicknameErrorTextView.setVisibility(View.INVISIBLE);
        nicknameErrorImageView.setVisibility(View.INVISIBLE);
    }

    private void noEmailError()
    {
        emailErrorTextView.setVisibility(View.INVISIBLE);
        emailErrorImageView.setVisibility(View.INVISIBLE);
    }

    private void noBirthdateError()
    {
        birthdateErrorTextView.setVisibility(View.INVISIBLE);
        birthdateErrorImageView.setVisibility(View.INVISIBLE);
    }

    private void noPasswordError()
    {
        passwordErrorTextView.setVisibility(View.INVISIBLE);
        passwordErrorImageView.setVisibility(View.INVISIBLE);
        confirmPasswordErrorTextView.setVisibility(View.INVISIBLE);
        confirmPasswordErrorImageView.setVisibility(View.INVISIBLE);
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