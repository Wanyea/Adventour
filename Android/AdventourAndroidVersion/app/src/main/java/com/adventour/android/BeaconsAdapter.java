package com.adventour.android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


public class BeaconsAdapter extends RecyclerView.Adapter<BeaconsAdapter.ViewHolder>
{
    private final Context context;
    private final ArrayList<BeaconsModel> beaconsArrayList;
    int numLikeShards = 10;

    public BeaconsAdapter(Context context, ArrayList<BeaconsModel> beaconsArrayList)
    {
        this.context = context;
        this.beaconsArrayList = beaconsArrayList;
    }

    @NonNull
    @Override
    public BeaconsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconsAdapter.ViewHolder holder, int position)
    {
        BeaconsModel model = beaconsArrayList.get(position);

        holder.beaconTitle.setText(model.getBeaconTitle());
        holder.beaconIntro.setText(model.getBeaconIntro());
        holder.beaconAuthor.setText(model.getBeaconAuthor());
        holder.beaconImage.setImageBitmap(model.getBeaconBitmap());
        holder.beaconCreatedDate.setText(model.getDateCreated());

        switch (model.getAndroidPfpRef())
        {
            // Set profile pic image to Cheetah
            case 0:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_cheetah);
                    break;

            // Set profile pic image to Elephant
            case 1:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_elephant);
                    break;

            // Set profile pic image to Ladybug
            case 2:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_ladybug);
                    break;

            // Set profile pic image to Monkey
            case 3:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_monkey);
                    break;

            // Set profile pic image to Fox
            case 4:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_fox);
                    break;

            // Set profile pic image to Penguin
            case 5:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_penguin);
                    break;

            // Set profile pic image to default
            default:
                holder.authorImageView.setImageResource(R.drawable.ic_user_icon);
        }

        holder.likesTextView.setText(String.valueOf(model.getNumOfLikes()));

        // Set Like ImageView when initializing RecyclerView
        setLikeImageView(model.getDocumentId(), holder.likeImageView);

        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                changeLike(model.getDocumentId(), holder.likeImageView);
                holder.likesTextView.setText(String.valueOf(model.getNumOfLikes()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return beaconsArrayList.size();
    }

    public void saveLike(String documentId)
    {
        Log.d("saveLike in BeaconsAdapter", "documentId: " + documentId);

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
                        Log.d("saveLike in BeaconAdapter", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().isEmpty())
                            {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    Log.d("saveLike in BeaconAdapter", "No snapshot found with this query! Adding a new like...");
                                    HashMap<String, String> newLike = new HashMap<String, String>();
                                    newLike.put("uid", user.getUid());
                                    newLike.put("beaconId", documentId);
                                    db.collection("Likes").add(newLike);

                                    Log.d("saveLike in BeaconAdapter", "Calling incrementLikesCounter...");
                                    incrementLikesCounter(documentId, numLikeShards);
                                }
                            }

                        }
                    }
                });
    }

    public void deleteLike(String documentId)
    {
        Log.d("deleteLike in BeaconsAdapter", "documentId: " + documentId);

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
                        Log.d("deleteLike in BeaconAdapter", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            Log.d("deleteLike in BeaconAdapter", "Removing like...");
                            db.collection("Likes")
                                    .document(document.getId())
                                    .delete();

                            Log.d("deleteLike in BeaconAdapter", "Calling decrementLikesCounter...");
                            decrementLikesCounter(documentId, numLikeShards);
                        }
                    }
                });
    }

    public void setLikeImageView(String documentId, ImageView likeImageView)
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
                        Log.d("hasUserLikePost in BeaconsAdapter", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().isEmpty())
                            {
                                Log.d("hasUserLikePost in BeaconsAdapter", "User has not liked this post.");
                                likeImageView.setImageResource(R.drawable.ic_heart_line_icon);
                            } else {
                                Log.d("hasUserLikePost in BeaconsAdapter", "User has liked this post.");
                                likeImageView.setImageResource(R.drawable.ic_heart_fill_icon);
                            }
                        }
                    }
                });
    }

    public void changeLike(String documentId, ImageView likeImageView)
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
                        Log.d("hasUserLikePost in BeaconsAdapter", "Failed calling database...");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().isEmpty())
                            {
                                Log.d("hasUserLikePost in BeaconsAdapter", "User has not liked this post previously.");
                                likeImageView.setImageResource(R.drawable.ic_heart_fill_icon);
                                saveLike(documentId);
                            } else {
                                Log.d("hasUserLikePost in BeaconsAdapter", "User has previously liked this post.");
                                likeImageView.setImageResource(R.drawable.ic_heart_line_icon);
                                deleteLike(documentId);
                            }
                        }
                    }
                });
    }

    public Task<Void> incrementLikesCounter(String documentId, final int numLikeShards)
    {
        Log.d("incrementLikesCounter in BeaconsAdapter", "documentId: " + documentId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference beaconRef = db.collection("Beacons").document(documentId);

        int shardId = (int) Math.floor(Math.random() * numLikeShards);
        DocumentReference shardRef = beaconRef.collection("likeShards").document(String.valueOf(shardId));

        return shardRef.update("count", FieldValue.increment(1));
    }

    public Task<Void> decrementLikesCounter(String documentId, final int numLikeShards)
    {
        Log.d("decrementLikesCounter in BeaconsAdapter", "documentId: " + documentId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference beaconRef = db.collection("Beacons").document(documentId);

        int shardId = (int) Math.floor(Math.random() * numLikeShards);
        DocumentReference shardRef = beaconRef.collection("likeShards").document(String.valueOf(shardId));

        return shardRef.update("count", FieldValue.increment(-1));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView beaconTitle;
        private final TextView beaconIntro;
        private final TextView beaconAuthor;
        private final ImageView beaconImage;
        private final ImageView authorImageView;
        private final TextView beaconCreatedDate;
        private final ImageView likeImageView;
        private final TextView likesTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            beaconTitle = itemView.findViewById(R.id.beaconTitleTextView);
            beaconIntro = itemView.findViewById(R.id.beaconDescriptionTextView);
            beaconAuthor = itemView.findViewById(R.id.authorTextView);
            beaconImage = itemView.findViewById(R.id.locationImageView);
            beaconCreatedDate = itemView.findViewById(R.id.beaconPostDate);
            authorImageView = itemView.findViewById(R.id.authorImageView);
            likeImageView = itemView.findViewById(R.id.likeImageView);
            likesTextView = itemView.findViewById(R.id.likesTextView);
        }
    }

}
