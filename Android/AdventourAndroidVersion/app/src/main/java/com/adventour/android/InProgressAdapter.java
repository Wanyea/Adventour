package com.adventour.android;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
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
    public final int CARD_TYPE = 1;
    public final int BUTTON_TYPE = 2;
    private final ArrayList<InProgressModel> InProgressModelArrayList;

    FirebaseAuth auth;
    FirebaseUser user;

    // Constructor
    public InProgressAdapter(Context context, ArrayList<InProgressModel> InProgressModelArrayList) {
        this.context = context;
        this.InProgressModelArrayList = InProgressModelArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == InProgressModelArrayList.size()) ? BUTTON_TYPE : CARD_TYPE;
    }

    @NonNull
    @Override
    public InProgressAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == CARD_TYPE) {
            // to inflate the layout for each item of recycler view.
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_progress_card_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.finish_adventour_button_layout, parent, false);
        }

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InProgressAdapter.Viewholder holder, int position) {
        if (position == InProgressModelArrayList.size()) {
            holder.finishAdventourButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("IN PROGRESS ADAPTER", "finish adventour button clicked!");
                    storeAdventour();
                    final Intent intent;
                    intent = new Intent(context, Congratulations.class);
                    context.startActivity(intent);
                }
            });
        } else {
            // to set data to textview of each card layout
            InProgressModel model = InProgressModelArrayList.get(position);
            holder.locationNameTextView.setText(model.getName());
        }


    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return InProgressModelArrayList.size() + 1;
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView locationNameTextView;
        private final Button finishAdventourButton;
        private final Context context;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.nameTextView);
            finishAdventourButton = itemView.findViewById(R.id.finishAdventourButton);
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