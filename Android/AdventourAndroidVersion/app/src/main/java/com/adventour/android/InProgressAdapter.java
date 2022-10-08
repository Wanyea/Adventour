package com.adventour.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class InProgressAdapter extends RecyclerView.Adapter<InProgressAdapter.Viewholder>
{

    private final Context context;
    private final ArrayList<InProgressModel> InProgressModelArrayList;

    // Constructor
    public InProgressAdapter(Context context, ArrayList<InProgressModel> InProgressModelArrayList)
    {
        this.context = context;
        this.InProgressModelArrayList = InProgressModelArrayList;
    }

    @NonNull
    @Override
    public InProgressAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_progress_card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InProgressAdapter.Viewholder holder, int position)
    {

        // to set data to textview of each card layout
        InProgressModel model = InProgressModelArrayList.get(position);
        holder.locationNameTextView.setText(model.getName());
    }

    @Override
    public int getItemCount()
    {
        // this method is used for showing number of card items in recycler view
        return InProgressModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class Viewholder extends RecyclerView.ViewHolder
    {
        private final TextView locationNameTextView;


        public Viewholder(@NonNull View itemView)
        {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.locationNameTextView);
        }
    }
}