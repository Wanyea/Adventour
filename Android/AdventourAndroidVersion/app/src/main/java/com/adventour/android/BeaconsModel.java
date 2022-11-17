package com.adventour.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconsModel {

    private String beaconTitle;
    private String beaconIntro;
    private String beaconAuthor;
    private Bitmap beaconBitmap;
    private String dateCreated;
    private String adventourId;
    private int androidPfpRef;
    private int numOfLikes;
    private String documentId;

    public BeaconsModel(Map allData, String userNickname, int androidPfpRef)
    {

        this.beaconTitle = (String) allData.get("title");
        this.beaconIntro = (String) allData.get("intro");
        this.beaconAuthor = userNickname;

        try {
            JSONArray locations = (JSONArray) allData.get("locations");
            JSONArray photos = (JSONArray) locations.getJSONObject(0).get("photos");
            String prefix = photos.getJSONObject(0).get("prefix").toString();
            String suffix = photos.getJSONObject(0).get("suffix").toString();

            URL imageURL = new URL(prefix + "original" + suffix);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            this.beaconBitmap = BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.adventourId = (String) allData.get("documentID");
        } catch (Error e) {
            System.out.println("That beacon can't be edited - it's old and missing an ID");
            e.printStackTrace();
        }

        this.dateCreated = AdventourUtils.formatBirthdateFromDatabase((Timestamp) allData.get("dateCreated"));
        this.androidPfpRef = androidPfpRef;

        if (allData.get("documentId") != null)
        {
            this.numOfLikes = getNumLikes((String) allData.get("documentId")).getResult();
            Log.d("numOfLikes in BeaconModel", String.valueOf(this.numOfLikes));
        }

        if (allData.get("documentId") != null)
        {
            this.documentId = (String) allData.get("documentId");
            Log.d("documentId in BeaconModel", documentId);
        }
    }

    public Task<Integer> getNumLikes(String documentId)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference beaconRef = db.collection("Beacons").document(documentId);

        // Sum the count of each shard in the subcollection
        return beaconRef.collection("likeShards").get()
                .continueWith(new Continuation<QuerySnapshot, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        int count = 0;
                        for (DocumentSnapshot snap : task.getResult())
                        {
                            LikeShard shard = snap.toObject(LikeShard.class);
                            count += shard.likeCount;
                        }
                        return count;
                    }
                });
    }


    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getBeaconTitle() {
        return beaconTitle;
    }

    public void setBeaconTitle(String beaconTitle) {
        this.beaconTitle = beaconTitle;
    }

    public String getBeaconIntro() {
        return beaconIntro;
    }

    public void setBeaconIntro(String beaconIntro) {
        this.beaconIntro = beaconIntro;
    }

    public String getBeaconAuthor() {
        return beaconAuthor;
    }

    public void setBeaconAuthor(String beaconAuthor) {
        this.beaconAuthor = beaconAuthor;
    }

    public Bitmap getBeaconBitmap() {
        return beaconBitmap;
    }

    public void setBeaconBitmap(Bitmap beaconBitmap) {
        this.beaconBitmap = beaconBitmap;
    }

    public int getAndroidPfpRef() {
        return androidPfpRef;
    }

    public void setProfilePicReference(int androidPfpRef) {
        this.androidPfpRef = androidPfpRef;
    }

    public String getAdventourId() {
        return this.adventourId;
    }

    public void setAdventourId(String adventourId) {
        this.adventourId = adventourId;
    }

    public int getNumOfLikes() {
        return numOfLikes;
    }

    public void setNumOfLikes(int numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
