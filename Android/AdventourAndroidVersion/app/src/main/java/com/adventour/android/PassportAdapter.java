//package com.adventour.android;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//public class PassportAdapter
//{
//    private final Context context;
//    private final ArrayList<PassportModel> beaconsArrayList;
//
//    public BeaconsAdapter(Context context, ArrayList<PassportModel> beaconsArrayList) {
//        this.context = context;
//        this.beaconsArrayList = beaconsArrayList;
//    }
//
//    @NonNull
//    @Override
//    public PassportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_card_layout, parent, false);
//        return new PassportAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull PassportAdapter.ViewHolder holder, int position) {
//        PassportModel model = beaconsArrayList.get(position);
//        holder.placesTextView.setText(model.getLocations());
//        holder.dateTextView.setText(model.getDate());
////        holder.locationImageView(model.getImageURL());
//    }
//
//    @Override
//    public int getItemCount() {
//        return beaconsArrayList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private final TextView placesTextView;
//        private final TextView dateTextView;
//        private final ImageView locationImageView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            placesTextView = itemView.findViewById(R.id.placesTextView);
//            dateTextView = itemView.findViewById(R.id.dateTextView);
//            locationImageView = itemView.findViewById(R.id.locationImageView);
//        }
//
//    }
//}
