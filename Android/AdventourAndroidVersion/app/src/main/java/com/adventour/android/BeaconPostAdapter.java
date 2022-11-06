package com.adventour.android;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BeaconPostAdapter extends RecyclerView.Adapter<BeaconPostAdapter.Viewholder> {

    private final Context context;
    private final ArrayList<BeaconPostModel> BeaconPostModelArrayList;

    // Constructor
    public BeaconPostAdapter(Context context, ArrayList<BeaconPostModel> BeaconPostModelArrayList)
    {
        this.context = context;
        this.BeaconPostModelArrayList = BeaconPostModelArrayList;
   
    }

    @NonNull
    @Override
    public BeaconPostAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_post_card_layout, parent, false);
        BeaconPostAdapter.Viewholder viewHolder = new BeaconPostAdapter.Viewholder(view/*, new CustomEditTextListener()*/);
            return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position)
    {

        // to set data to textview of each card layout
        BeaconPostModel model = BeaconPostModelArrayList.get(position);

        holder.locationNameTextView.setText(model.getName());
        holder.ratingBar.setRating(model.getRating());
        holder.addressTextView.setText(model.getAddress());
        holder.locationOneImageView.setImageBitmap(model.getLocationImages().locationOne);
        holder.locationTwoImageView.setImageBitmap(model.getLocationImages().locationTwo);
        holder.locationThreeImageView.setImageBitmap(model.getLocationImages().locationThree);

        //holder.customEditTextListener.updatePosition(position);

        holder.locationDescriptionEditText.setText(model.getDescription());

        if(position >= GlobalVars.locationDescriptions.size())
        {
            Log.d("position", String.valueOf(position));
            Log.d("getLayoutPos", String.valueOf(holder.getLayoutPosition()));
            GlobalVars.locationDescriptions.add(position, model.getDescription());
            Log.d("locationDes if empty ", GlobalVars.locationDescriptions.toString());
        } else {
            Log.d("position in else", String.valueOf(position));
            Log.d("getLayoutPos in else", String.valueOf(holder.getLayoutPosition()));
            GlobalVars.locationDescriptions.set(position, model.getDescription());
            Log.d("locationDes in else ", GlobalVars.locationDescriptions.toString());
        }

        holder.locationDescriptionEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged. LP", String.valueOf(holder.getLayoutPosition()));
                Log.d("locationDes onChange ", GlobalVars.locationDescriptions.toString());
                GlobalVars.locationDescriptions.set(position, s.toString());
            }
        });


        /*if (GlobalVars.locationDescriptions.isEmpty())
        {
            holder.locationDescriptionEditText.setText(model.getDescription());
            GlobalVars.locationDescriptions.add(position, model.getDescription());
        } else if (position < GlobalVars.locationDescriptions.size()) {
            Log.d("BEACON_POST_AD", "locationDescriptions not null.");
            Log.d("BEACON_POST_AD", GlobalVars.locationDescriptions.toString());
            holder.locationDescriptionEditText.setText(GlobalVars.locationDescriptions.get(position));

        }*/
    }

    @Override
    public int getItemCount()
    {
        // this method is used for showing number of card items in recycler view
        return BeaconPostModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class Viewholder extends RecyclerView.ViewHolder
    {
        private final TextView locationNameTextView;
        private final RatingBar ratingBar;
        private final TextView addressTextView;
        private final ImageView locationOneImageView;
        private final ImageView locationTwoImageView;
        private final ImageView locationThreeImageView;

        public EditText locationDescriptionEditText;
        //public CustomEditTextListener customEditTextListener;

        public Viewholder(@NonNull View itemView/*, CustomEditTextListener customEditTextListener*/)
        {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.nameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            locationOneImageView = itemView.findViewById(R.id.locationOneImageView);
            locationTwoImageView = itemView.findViewById(R.id.locationTwoImageView);
            locationThreeImageView = itemView.findViewById(R.id.locationThreeImageView);
            locationDescriptionEditText = (EditText) itemView.findViewById(R.id.descriptionEditText);


         /*   this.customEditTextListener = customEditTextListener;
            this.locationDescriptionEditText.addTextChangedListener(customEditTextListener);*/

        }
    }

    /*private class CustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position)
        {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            Log.d("BEACON_POST_AD", "Inside onTextChanged. Before if statement");

            if(GlobalVars.locationDescriptions.isEmpty())
            {
                Log.d("BEACON_POST_AD", "Inside if statement");
                Log.d("Position ", String.valueOf(position));
                Log.d("LOC DES ", GlobalVars.locationDescriptions.toString());

                GlobalVars.locationDescriptions.add(position, charSequence.toString());
            } else {
                Log.d("BEACON_POST_AD", "Inside else statement");
                Log.d("Position in else", String.valueOf(position));
                Log.d("LOC DES IN ELSE", GlobalVars.locationDescriptions.toString());
                GlobalVars.locationDescriptions.set(position, charSequence.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable)
        {

        }
        */
}
