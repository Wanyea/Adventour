package com.adventour.android;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InProgressAdapter extends RecyclerView.Adapter<InProgressAdapter.Viewholder> {

    private final Context context;
    private final ArrayList<InProgressModel> InProgressModelArrayList;

    // Constructor
    public InProgressAdapter(Context context, ArrayList<InProgressModel> InProgressModelArrayList) {
        this.context = context;
        this.InProgressModelArrayList = InProgressModelArrayList;
    }

    @NonNull
    @Override
    public InProgressAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

            // to inflate the layout for each item of recycler view.
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_progress_card_layout, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InProgressAdapter.Viewholder holder, int position) {
            // to set data to textview of each card layout
            InProgressModel model = InProgressModelArrayList.get(position);
            holder.locationNameTextView.setText(model.getName());
    }




    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return InProgressModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView locationNameTextView;
        private final Context context;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.nameTextView);
            context = itemView.getContext();
        }
    }

    public void storeAdventour()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> adventour = new HashMap<>();
        adventour.put("dateCreated", new Date());
        adventour.put("locations", GlobalVars.adventourFSQIds);
        adventour.put("numLocations", GlobalVars.adventourFSQIds.size());


        db.collection("Adventourists")
                .document(user.getUid())
                .collection("adventours")
                .add(adventour)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("In Progress Adapter", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("In Progress Adapter", "Error adding document", e);
                    }
                });
    }
}