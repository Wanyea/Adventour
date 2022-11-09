package com.adventour.android;

import static java.lang.Math.toIntExact;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    CardView popupProfPic;
    Button doneButton;
    ImageButton profPicImageButton;
    ImageButton cheetahImageButton, elephantImageButton, ladybugImageButton, monkeyImageButton, penguinImageButton, foxImageButton;
    ProfilePictureRefs refs;

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
        refs = new ProfilePictureRefs(0, "");

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
            public void onClick(View view) { onClickElephantImageButton(view); }
        });

        ladybugImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) { onClickLadybugImageButton(view); }
        });

        monkeyImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) { onClickMonkeyImageButton(view); }
        });

        penguinImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) { onClickPenguinImageButton(view); }
        });

        foxImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) { onClickFoxImageButton(view); }
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
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        int profPicID = toIntExact((long)document.get("androidPfpRef"));

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        emailEditText.setText(document.getString("email"));
                        nicknameEditText.setText(document.getString("nickname"));
                        firstNameEditText.setText(document.getString("firstName"));
                        lastNameEditText.setText(document.getString("lastName"));
                        birthdateTextView.setText(AdventourUtils.formatBirthdateFromDatabase((Timestamp) document.get("birthdate")));
                        mantraEditText.setText(document.getString("mantra"));
                        profPicImageButton.setForeground(getResources().getDrawable(profPicID, null));

                        email = emailEditText.getText().toString();
                        nickname = nicknameEditText.getText().toString();
                        mantra = mantraEditText.getText().toString();
                        firstName = firstNameEditText.getText().toString();
                        lastName = lastNameEditText.getText().toString();
                        birthdate = ((Timestamp) document.get("birthdate"));
                        androidPfpRef = profPicID;


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

    public void onClickProfPicImageButton(View view) {
        popupProfPic.setVisibility(View.VISIBLE);
    }


    class ProfilePictureRefs
    {
        private int androidProfilePicRef;
        private String iOSProfilePicRef;

        ProfilePictureRefs(int androidProfilePicRef, String iOSProfilePicRef)
        {
            this.androidProfilePicRef = androidProfilePicRef;
            this.iOSProfilePicRef = iOSProfilePicRef;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickCheetahImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_cheetah, cheetahImageButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickElephantImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_elephant, elephantImageButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickLadybugImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_ladybug, ladybugImageButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickMonkeyImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_monkey, monkeyImageButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickPenguinImageButton(View view) {
        clearProfPicSelection(view);
        penguinImageButton.setForeground(getResources().getDrawable(R.drawable.rectangle_blue_variant));
        changeProfPic(view, R.drawable.ic_profpic_penguin, penguinImageButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickFoxImageButton(View view) {
        changeProfPic(view, R.drawable.ic_profpic_fox, foxImageButton);
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

        refs.androidProfilePicRef = drawableID;
        profPicImageButton.setForeground(getResources().getDrawable(drawableID, null));
        androidPfpRef = drawableID;
    }
}