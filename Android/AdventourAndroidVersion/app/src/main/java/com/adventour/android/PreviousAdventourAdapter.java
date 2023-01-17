package com.adventour.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class PreviousAdventourAdapter extends RecyclerView.Adapter<PreviousAdventourAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<PreviousAdventourModel> PreviousAdventourArrayList;

    public PreviousAdventourAdapter(Context context, ArrayList<PreviousAdventourModel> PreviousAdventourArrayList) {
        this.context = context;
        this.PreviousAdventourArrayList = PreviousAdventourArrayList;
    }

    @NonNull
    @Override
    public PreviousAdventourAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_adventour_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousAdventourAdapter.ViewHolder holder, int position)
    {
        PreviousAdventourModel model = PreviousAdventourArrayList.get(position);

        if (model.getFirstLocation() != null)
        {
            holder.firstLocation.setText(model.getFirstLocation());
        } else {
            holder.firstLocation.setText("");
        }

        if (model.getSecondLocation() != null)
        {
            holder.secondLocation.setText(model.getSecondLocation());
        } else {
            holder.secondLocation.setText("");
        }

        if (model.getThirdLocation() != null)
        {
            holder.thirdLocation.setText(model.getThirdLocation());
        } else {
            holder.thirdLocation.setText("");
        }

        holder.dateCreated.setText(model.getDateCreated());
    }

    @Override
    public int getItemCount() {
        return PreviousAdventourArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView firstLocation;
        private final TextView secondLocation;
        private final TextView thirdLocation;
        private  final TextView dateCreated;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstLocation = itemView.findViewById(R.id.firstLocation);
            secondLocation = itemView.findViewById(R.id.secondLocation);
            thirdLocation = itemView.findViewById(R.id.thirdLocation);
            dateCreated = itemView.findViewById(R.id.dateTextView);
        }

    }
}
