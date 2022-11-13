package com.adventour.android;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignupScreen extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    static String defaultMantra = "I love to go on many Adventours!";

    final Calendar birthdateCalendar = Calendar.getInstance();
    String nickname, email, password, confirmPassword;
    Date birthdate;
    Button signupButton;
    TextView loginTextView, nicknameErrorTextView, emailErrorTextView, birthdateErrorTextView, passwordErrorTextView, confirmPasswordErrorTextView, birthdateDatePicker, privacyPolicyTOSTextView;
    ImageView nicknameErrorImageView, emailErrorImageView, birthdateErrorImageView, passwordErrorImageView, confirmPasswordErrorImageView;
    EditText nicknameEditText, emailEditText, birthdateEditText, passwordEditText, confirmPasswordEditText;
    int day, month, year;
    long androidPfpRef;
    String iOSPfpRef;
    ImageButton profPicImageButton;
    CardView popupProfPic;
    Button doneButton;
    ImageButton cheetahImageButton, elephantImageButton, ladybugImageButton, monkeyImageButton, penguinImageButton, foxImageButton;

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
        androidPfpRef = 6;
        iOSPfpRef = "";

        loginTextView = (TextView) findViewById(R.id.loginTextView);
        signupButton = (Button) findViewById(R.id.signupButton);
        birthdateDatePicker = (TextView) findViewById(R.id.birthdateTextView);

        nicknameErrorTextView = (TextView) findViewById(R.id.nicknameSignupErrorTextView);
        emailErrorTextView = (TextView) findViewById(R.id.emailSignupErrorTextView);
        birthdateErrorTextView = (TextView) findViewById(R.id.birthdateSignupErrorTextView);
        passwordErrorTextView = (TextView) findViewById(R.id.passwordSignupErrorTextView);
        confirmPasswordErrorTextView = (TextView) findViewById(R.id.confirmPasswordSignupErrorTextView);

        privacyPolicyTOSTextView = (TextView) findViewById(R.id.privacyPolicyTOSTextView);
        privacyPolicyTOSTextView.setMovementMethod(LinkMovementMethod.getInstance());

        nicknameErrorImageView = (ImageView) findViewById(R.id.nicknameSignupErrorIcon);
        emailErrorImageView = (ImageView) findViewById(R.id.emailSignupErrorIcon);
        birthdateErrorImageView = (ImageView) findViewById(R.id.birthdateSignupErrorIcon);
        passwordErrorImageView = (ImageView) findViewById(R.id.passwordSignupErrorIcon);
        confirmPasswordErrorImageView = (ImageView) findViewById(R.id.confirmPasswordSignupErrorIcon);

        nicknameEditText = (EditText) findViewById(R.id.nicknameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);

        // Profile picture functionality
        profPicImageButton = (ImageButton) findViewById(R.id.profPicImageButton);
        popupProfPic = (CardView) findViewById(R.id.popupProfPic);
        popupProfPic.setVisibility(View.INVISIBLE);
        cheetahImageButton = (ImageButton) findViewById(R.id.cheetahImageButton);
        elephantImageButton = (ImageButton) findViewById(R.id.elephantImageButton);
        ladybugImageButton = (ImageButton) findViewById(R.id.ladybugImageButton);
        monkeyImageButton = (ImageButton) findViewById(R.id.monkeyImageButton);
        penguinImageButton = (ImageButton) findViewById(R.id.penguinImageButton);
        foxImageButton = (ImageButton) findViewById(R.id.foxImageButton);
        doneButton = (Button) findViewById(R.id.doneButton);

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
                password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString().trim();
                confirmPassword = ((EditText) findViewById(R.id.confirmPasswordEditText)).getText().toString().trim();

                // Parse birthdate
                String myFormat = "MM/dd/yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                String date = dateFormat.format(birthdateCalendar.getTime());
                day = Integer.parseInt(date.substring(0, 2));
                month = Integer.parseInt(date.substring(3, 5));
                year = Integer.parseInt(date.substring(6, 10));

                birthdate = birthdateCalendar.getTime();

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

                // Check if email is valid
                if (!AdventourUtils.isValidPassword(password)) {
                    displayPasswordError();
                } else {
                    noPasswordError();
                }

                // Check passwords match
                if (!AdventourUtils.checkPasswordsMatch(password, confirmPassword))
                {
                    displayConfirmPasswordError();
                } else {
                    noConfirmPasswordError();
                }

                // If all fields are valid: create new document in Firebase with the user information.

                if (AdventourUtils.isValidNickname(nickname) &&
                   AdventourUtils.isValidEmail(email) &&
                   AdventourUtils.isUserOver13(day, month, year) &&
                   AdventourUtils.checkPasswordsMatch(password, confirmPassword) &&
                   AdventourUtils.isProfilePictureSelected(androidPfpRef))
                    {
                        signUp(nickname, email, password, birthdate, defaultMantra, androidPfpRef, iOSPfpRef); // Attempt to create user document and add to firebase.
                    }
            }
        });

        // Profile pic
        profPicImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onClickProfPicImageButton(view); }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupProfPic.setVisibility(View.INVISIBLE);
            }
        });

        cheetahImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view)
            {
                onClickCheetahImageButton(view);
            }
        });

        elephantImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                onClickElephantImageButton(view);
            }
        });

        ladybugImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                onClickLadybugImageButton(view);
            }
        });

        monkeyImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                onClickMonkeyImageButton(view);
            }
        });

        penguinImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                onClickPenguinImageButton(view);
            }
        });

        foxImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                onClickFoxImageButton(view);
            }
        });
    }

    private void displayNicknameError() {
        Log.d("NicknameInvalid", "Nickname must be between 6 and 20 characters.");

        // Render error message and icon, make edit text 'red_variant'

        int red_variant = getResources().getColor(R.color.red_variant);
        nicknameEditText.getBackground().setColorFilter(red_variant, PorterDuff.Mode.SRC_ATOP);
        nicknameErrorTextView.setVisibility(View.VISIBLE);
        nicknameErrorImageView.setVisibility(View.VISIBLE);
    }

    private void displayEmailError() {
        Log.d("EmailInvalid", "Invalid email.");

        // Render error message and icon, make edit text 'red_variant'
        int red_variant = getResources().getColor(R.color.red_variant);
        emailEditText.getBackground().setColorFilter(red_variant, PorterDuff.Mode.SRC_ATOP);
        emailErrorTextView.setVisibility(View.VISIBLE);
        emailErrorImageView.setVisibility(View.VISIBLE);
    }

    private void displayBirthdateError() {
        Log.d("BirthdateInvalid", "User is not 13 years of age.");

        // Render error message and icon, make edit text 'red_variant'
        int red_variant = getResources().getColor(R.color.red_variant);
        birthdateEditText.getBackground().setColorFilter(red_variant, PorterDuff.Mode.SRC_ATOP);
        birthdateErrorTextView.setVisibility(View.VISIBLE);
        birthdateErrorImageView.setVisibility(View.VISIBLE);

    }
    
    private void displayPasswordError() {
        Log.d("Invalid password", "Invalid pass, missing components.");

        // Render error message and icon, make edit text 'red_variant'

        int red_variant = getResources().getColor(R.color.red_variant);

        passwordEditText.getBackground().setColorFilter(red_variant, PorterDuff.Mode.SRC_ATOP);
        passwordErrorTextView.setVisibility(View.VISIBLE);
        passwordErrorImageView.setVisibility(View.VISIBLE);

    }

    private void displayConfirmPasswordError() {
        int red_variant = getResources().getColor(R.color.red_variant);

        confirmPasswordEditText.getBackground().setColorFilter(red_variant, PorterDuff.Mode.SRC_ATOP);
        confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
        confirmPasswordErrorImageView.setVisibility(View.VISIBLE);
    }


    private void displaySignUpError() {
        Log.d("BadSignUp", "There was a problem with sign up, please try again later.");
    }

    private void noNicknameError()
    {
        // Update handled errors by removing error message and icon, update edit text to 'blue_main'
        int blue_main = getResources().getColor(R.color.blue_main);
        nicknameEditText.getBackground().setColorFilter(blue_main, PorterDuff.Mode.SRC_ATOP);
        nicknameErrorTextView.setVisibility(View.INVISIBLE);
        nicknameErrorImageView.setVisibility(View.INVISIBLE);
    }

    private void noEmailError()
    {
        // Update handled errors by removing error message and icon, update edit text to 'blue_main'
        int blue_main = getResources().getColor(R.color.blue_main);
        emailEditText.getBackground().setColorFilter(blue_main, PorterDuff.Mode.SRC_ATOP);
        emailErrorTextView.setVisibility(View.INVISIBLE);
        emailErrorImageView.setVisibility(View.INVISIBLE);
    }

    private void noBirthdateError()
    {
        // Update handled errors by removing error message and icon, update edit text to 'blue_main'
        int blue_main = getResources().getColor(R.color.blue_main);
        birthdateErrorTextView.setVisibility(View.INVISIBLE);
        birthdateErrorImageView.setVisibility(View.INVISIBLE);
    }

    private void noPasswordError()
    {
        // Update handled errors by removing error message and icon, update edit text to 'blue_main'
        int blue_main = getResources().getColor(R.color.blue_main);
        passwordEditText.getBackground().setColorFilter(blue_main, PorterDuff.Mode.SRC_ATOP);
        passwordErrorTextView.setVisibility(View.INVISIBLE);
        passwordErrorImageView.setVisibility(View.INVISIBLE);
    }

    private void noConfirmPasswordError() {
        // Update handled errors by removing error message and icon, update edit text to 'blue_main'
        int blue_main = getResources().getColor(R.color.blue_main);
        confirmPasswordEditText.getBackground().setColorFilter(blue_main, PorterDuff.Mode.SRC_ATOP);
        confirmPasswordErrorTextView.setVisibility(View.INVISIBLE);
        confirmPasswordErrorImageView.setVisibility(View.INVISIBLE);
    }

    private void updateLabel(){
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        birthdateDatePicker.setText("Birthdate - " + dateFormat.format(birthdateCalendar.getTime()));
    }

    private void addUserToFirestore(String nickname, String email, Date birthdate, String defaultMantra, long androidPfpRef, String iOSPfpRef) {

        Map<String, Object> adventourist = new HashMap<>();
        adventourist.put("nickname", nickname);
        adventourist.put("email", email);
        adventourist.put("birthdate", birthdate);
        adventourist.put("isPrivate", true);
        adventourist.put("mantra", defaultMantra);
        adventourist.put("androidPfpRef", androidPfpRef);
        adventourist.put("iOSPfpRef", iOSPfpRef);

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

    private void signUp(String nickname, String email, String password, Date birthdate, String defaultMantra, long androidPfpRef, String iOSPfpRef) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            addUserToFirestore(nickname, email, birthdate, defaultMantra, androidPfpRef, iOSPfpRef);
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

    public void onClickProfPicImageButton(View view) {
        popupProfPic.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickCheetahImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_cheetah, cheetahImageButton);
        androidPfpRef = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickElephantImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_elephant, elephantImageButton);
        androidPfpRef = 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickLadybugImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_ladybug, ladybugImageButton);
        androidPfpRef = 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickMonkeyImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_monkey, monkeyImageButton);
        androidPfpRef = 3;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickFoxImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_fox, foxImageButton);
        androidPfpRef = 4;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickPenguinImageButton(View view) {
        clearProfPicSelection(view);
        changeProfPic(view, R.drawable.ic_profpic_penguin, penguinImageButton);
        androidPfpRef = 5;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clearProfPicSelection(View view) {
        cheetahImageButton.setForeground(null);
        elephantImageButton.setForeground(null);
        ladybugImageButton.setForeground(null);
        monkeyImageButton.setForeground(null);
        penguinImageButton.setForeground(null);
        foxImageButton.setForeground(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void changeProfPic(View view, int drawableID, ImageButton imageButton) {

        clearProfPicSelection(view);
        imageButton.setForeground(getResources().getDrawable(R.drawable.rectangle_blue_variant));
        profPicImageButton.setForeground(getResources().getDrawable(drawableID, null));
    }
}