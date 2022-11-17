package com.adventour.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AdventourSummaryAdapter extends RecyclerView.Adapter<AdventourSummaryAdapter.Viewholder> {
    private final Context context;
    private final ArrayList<AdventourSummaryModel> adventourArrayList;

    // Constructor
    public AdventourSummaryAdapter (Context context, ArrayList<AdventourSummaryModel> adventourArrayList) {
        this.context = context;
        this.adventourArrayList = adventourArrayList;
    }

    @NonNull
    @Override
    public AdventourSummaryAdapter.Viewholder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adventour_ticket_card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdventourSummaryAdapter.Viewholder holder, int position) {
        AdventourSummaryModel model = adventourArrayList.get(position);
        holder.locationNameTextView.setText(model.getName());
        holder.descriptionTextView.setText(model.getDescription());
        holder.imageView.setImageBitmap(model.getImage());
    }

    @Override
    public int getItemCount() {
        return adventourArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView locationNameTextView;
        private final TextView descriptionTextView;
        private final ImageView imageView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.nameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionEditText);
            imageView = itemView.findViewById(R.id.locationImageView);
        }
    }
}
