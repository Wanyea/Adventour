package com.adventour.android;

import android.content.Context;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
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
        BeaconPostAdapter.Viewholder viewHolder = new BeaconPostAdapter.Viewholder(view, new CustomEditTextListener());
            return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconPostAdapter.Viewholder holder, int position)
    {

        // to set data to textview of each card layout
        BeaconPostModel model = BeaconPostModelArrayList.get(position);

        holder.locationNameTextView.setText(model.getName());
        holder.ratingBar.setRating(model.getRating());
        holder.addressTextView.setText(model.getAddress());
        holder.locationOneImageView.setImageBitmap(model.getLocationImages().locationOne);
        holder.locationTwoImageView.setImageBitmap(model.getLocationImages().locationTwo);
        holder.locationThreeImageView.setImageBitmap(model.getLocationImages().locationThree);

        holder.customEditTextListener.updatePosition(position);

        if (GlobalVars.locationDescriptions == null)
        {
            holder.locationDescriptionEditText.setText(model.getDescription());
        } else {
            holder.locationDescriptionEditText.setText(GlobalVars.locationDescriptions.get(position));

        }
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
        private final ImageView locationOneImageView;
        private final ImageView locationTwoImageView;
        private final ImageView locationThreeImageView;

        public EditText locationDescriptionEditText;
        public CustomEditTextListener customEditTextListener;

        public Viewholder(@NonNull View itemView, CustomEditTextListener customEditTextListener)
        {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.nameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            locationOneImageView = itemView.findViewById(R.id.locationOneImageView);
            locationTwoImageView = itemView.findViewById(R.id.locationTwoImageView);
            locationThreeImageView = itemView.findViewById(R.id.locationThreeImageView);

            this.locationDescriptionEditText = (EditText) itemView.findViewById(R.id.descriptionEditText);
            this.customEditTextListener = customEditTextListener;
            this.locationDescriptionEditText.addTextChangedListener(customEditTextListener);

        }
    }

    private class CustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if(GlobalVars.locationDescriptions != null)
                GlobalVars.locationDescriptions.add(position, charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }
}
