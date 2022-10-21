package com.adventour.android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class InProgressAdapter extends RecyclerView.Adapter<InProgressAdapter.Viewholder>
{

    private final Context context;
    public final int CARD_TYPE = 1;
    public final int BUTTON_TYPE = 2;
    private final ArrayList<InProgressModel> InProgressModelArrayList;


    // Constructor
    public InProgressAdapter(Context context, ArrayList<InProgressModel> InProgressModelArrayList)
    {
        this.context = context;
        this.InProgressModelArrayList = InProgressModelArrayList;
    }

    @Override
    public int getItemViewType(int position) {
       return (position == InProgressModelArrayList.size()) ? BUTTON_TYPE : CARD_TYPE;
    }

    @NonNull
    @Override
    public InProgressAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        if (viewType == CARD_TYPE)
        {
            // to inflate the layout for each item of recycler view.
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_progress_card_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.finish_adventour_button_layout, parent, false);
        }

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InProgressAdapter.Viewholder holder, int position)
    {
        if(position == InProgressModelArrayList.size()) {
            holder.finishAdventourButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("IN PROGRESS ADAPTER", "finish adventour button clicked!");
                }
            });
        } else {
            // to set data to textview of each card layout
            InProgressModel model = InProgressModelArrayList.get(position);
            holder.locationNameTextView.setText(model.getName());
        }


    }

    @Override
    public int getItemCount()
    {
        // this method is used for showing number of card items in recycler view
        return InProgressModelArrayList.size() + 1;
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class Viewholder extends RecyclerView.ViewHolder
    {
        private final TextView locationNameTextView;
        private final Button finishAdventourButton;


        public Viewholder(@NonNull View itemView)
        {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.nameTextView);
            finishAdventourButton = itemView.findViewById(R.id.finishAdventourButton);
        }
    }
}