package com.adventour.android;

import static java.lang.Math.toIntExact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditPassport extends AppCompatActivity {
    
    private static final String TAG = "PassportMoreInfo";

    EditText emailEditText, nicknameEditText, mantraEditText, firstNameEditText, lastNameEditText;
    String email, nickname, firstName, lastName, mantra;
    FloatingActionButton saveButton;
    ImageView backButton;
    TextView birthdateTextView;
    Timestamp birthdate;
    int androidPfpRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_passport);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        nicknameEditText = (EditText) findViewById(R.id.nicknameEditText);
        mantraEditText = (EditText) findViewById(R.id.mantraEditText);
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);

        birthdateTextView = (TextView) findViewById(R.id.birthdateTextView);


        saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        backButton = (ImageView) findViewById(R.id.backButton);

        // Make a call to the DB and populate EditTexts on this Activity with user information.
        populateUserPassport();

        // Build AlertDialog that will alert users when back button is pressed.
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Are you sure you don't want to save your Passport changes?");
        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        switchToPassport();
                    }
                });

        alertBuilder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Detect and store any changes made to EditTexts.
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                email = emailEditText.getText().toString();
            }
        });

        nicknameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                nickname = nicknameEditText.getText().toString();
            }
        });

        mantraEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mantra = mantraEditText.getText().toString();
            }
        });

        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                firstName = firstNameEditText.getText().toString();
            }
        });

        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                lastName = lastNameEditText.getText().toString();
            }
        });

        // When saveButton is pressed save changes to DB.
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Map<String, Object> changes = new HashMap<>();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Get a reference to the user
                DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());

                // Check if user document exists. If they do in this instance, populate passport wth user data.
                documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                changes.put("email", email);
                                changes.put("nickname", nickname);
                                changes.put("mantra", mantra);
                                changes.put("firstName", firstName);
                                changes.put("lastName", lastName);
                                changes.put("birthdate", birthdate);
                                changes.put("androidPfpRef", androidPfpRef);
                                //TODO: WE NEED TO PUT THE IOS ONE HERE OR IT'LL BE CLEARED ON THEIR END.

                                db.collection("Adventourists").document(user.getUid())
                                        .set(changes)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                switchToPassport();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);

                                                // Possible ALERT when there is an error prompting users to try again.
                                            }
                                        });

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        });

/*      // Supposed to handle back button gesture but doesn't work on my Pixel 6, maybe only works with physical android buttons?
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        };

*/
    }

    public void populateUserPassport()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the user
        DocumentReference documentRef = db.collection("Adventourists").document(user.getUid());

        // Check if user document exists. If they do in this instance, populate passport wth user data.
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        emailEditText.setText(document.getString("email"));
                        nicknameEditText.setText(document.getString("nickname"));
                        firstNameEditText.setText(document.getString("firstName"));
                        lastNameEditText.setText(document.getString("lastName"));
                        birthdateTextView.setText(AdventourUtils.formatBirthdateFromDatabase((Timestamp) document.get("birthdate")));
                        mantraEditText.setText(document.getString("mantra"));

                        email = emailEditText.getText().toString();
                        nickname = nicknameEditText.getText().toString();
                        mantra = mantraEditText.getText().toString();
                        firstName = firstNameEditText.getText().toString();
                        lastName = lastNameEditText.getText().toString();
                        birthdate = ((Timestamp) document.get("birthdate"));
                        androidPfpRef = (toIntExact((long)document.get("androidPfpRef")));


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void switchToPassport()
    {
        Intent intent = new Intent(this, Passport.class);
        startActivity(intent);
        finish();
    }
}