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
    private int androidPfpRef;
    private int numOfLikes;
    private String documentId;


    public class LikeCounter {
        int numLikeShards;

        public LikeCounter(int numLikeShards) {
            this.numLikeShards = numLikeShards;
        }
    }

    public class LikeShard {
        int likeCount;

        public LikeShard(int likeCount) {
            this.likeCount = likeCount;
        }
    }

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

    public Task<Void> createLikeCounter(final DocumentReference ref, final int numLikeShards)
    {
        // Initialize the counter document, then initialize each shard.
        return ref.set(new BeaconsModel.LikeCounter(numLikeShards))
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        List<Task<Void>> tasks = new ArrayList<>();

                        // Initialize each shard with count=0
                        for (int i = 0; i < numLikeShards; i++) {
                            Task<Void> makeShard = ref.collection("likeShards")
                                    .document(String.valueOf(i))
                                    .set(new BeaconsModel.LikeShard(0));

                            tasks.add(makeShard);
                        }

                        return Tasks.whenAll(tasks);
                    }
                });
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
                            BeaconsModel.LikeShard shard = snap.toObject(BeaconsModel.LikeShard.class);
                            count += shard.likeCount;
                        }
                        return count;
                    }
                });
    }

    public void checkLiked(String documentId)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Likes")
                .whereEqualTo("uid", user.getUid())
                .whereEqualTo("beaconID", documentId)
                .get()
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.d("checkLiked in Beacons", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d("checkLiked in Beacons", "Documents retrieved!");
                            for (QueryDocumentSnapshot likes : task.getResult())
                            {
                                Map<String, Object> userLikes = new HashMap<>();
                                userLikes = likes.getData();
                                Log.d("checkedLike in BeaconsModel", "User likes data: " + userLikes);
                            }
                        }
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
