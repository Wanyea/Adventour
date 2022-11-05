package com.adventour.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        return new BeaconPostAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconPostAdapter.Viewholder holder, int position)
    {

        // to set data to textview of each card layout
        BeaconPostModel model = BeaconPostModelArrayList.get(position);

        holder.locationNameTextView.setText(model.getName());
        holder.ratingBar.setRating(model.getRating());
        holder.addressTextView.setText(model.getAddress());
        holder.locationDescriptionTextView.setText(model.getDescription());
        holder.locationOneImageView.setImageBitmap(model.getLocationImages().locationOne);
        holder.locationTwoImageView.setImageBitmap(model.getLocationImages().locationTwo);
        holder.locationThreeImageView.setImageBitmap(model.getLocationImages().locationThree);


    }

    @Override
    public int getItemCount()
    {
        // this method is used for showing number of card items in recycler view
        return BeaconPostModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class Viewholder extends RecyclerView.ViewHolder
    {
        private final TextView locationNameTextView;
        private final RatingBar ratingBar;
        private final TextView addressTextView;
        private final TextView locationDescriptionTextView;
        private final ImageView locationOneImageView;
        private final ImageView locationTwoImageView;
        private final ImageView locationThreeImageView;



        public Viewholder(@NonNull View itemView)
        {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.nameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            locationDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            locationOneImageView = itemView.findViewById(R.id.locationOneImageView);
            locationTwoImageView = itemView.findViewById(R.id.locationTwoImageView);
            locationThreeImageView = itemView.findViewById(R.id.locationThreeImageView);

        }
    }
}
