package com.adventour.android;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditPassport extends AppCompatActivity {
    
    private static final String TAG = "PassportMoreInfo";

    EditText emailEditText, nicknameEditText, birthdateEditText, mantraEditText, firstNameEditText, lastNameEditText;
    String email, nickname, birthdate, firstName, lastName, mantra;
    String changedEmail, changedNickname, changedBirthdate, changedFirstName, changedLastName, changedMantra;
    FloatingActionButton saveButton;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_passport);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        nicknameEditText = (EditText) findViewById(R.id.nicknameEditText);
        birthdateEditText = (EditText) findViewById(R.id.birthdateEditText);
        mantraEditText = (EditText) findViewById(R.id.mantraEditText);
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);

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
                changedEmail = emailEditText.getText().toString();
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
                changedNickname = nicknameEditText.getText().toString();
            }
        });

        birthdateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                changedBirthdate = birthdateEditText.getText().toString();
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
                changedMantra = mantraEditText.getText().toString();
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
                changedFirstName = firstNameEditText.getText().toString();
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
                changedLastName = lastNameEditText.getText().toString();
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

                                if (!email.equals(changedEmail)) { changes.put("email", changedEmail); }
                                if (!nickname.equals(changedNickname)) { changes.put("nickname", changedNickname); }
                                if (!birthdate.equals(changedBirthdate)) { changes.put("birthdate", changedBirthdate); }
                                if (!mantra.equals(changedMantra)) { changes.put("mantra", changedMantra); }
                                if (!firstName.equals(changedFirstName)) { changes.put("firstName", changedFirstName); }
                                if (!lastName.equals(changedLastName)) { changes.put("lastName", changedLastName); }

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
                        birthdateEditText.setText(document.getString("birthdate"));
                        mantraEditText.setText(document.getString("mantra"));

                        email = emailEditText.getText().toString();
                        nickname = nicknameEditText.getText().toString();
                        birthdate = birthdateEditText.getText().toString();
                        mantra = mantraEditText.getText().toString();
                        firstName = firstNameEditText.getText().toString();
                        lastName = lastNameEditText.getText().toString();


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