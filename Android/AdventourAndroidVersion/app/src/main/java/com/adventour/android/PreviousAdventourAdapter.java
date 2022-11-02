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
        holder.firstLocation.setText(model.getFirstLocation());
        holder.secondLocation.setText(model.getSecondLocation());
        holder.thirdLocation.setText(model.getThirdLocation());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstLocation = itemView.findViewById(R.id.firstLocation);
            secondLocation = itemView.findViewById(R.id.secondLocation);
            thirdLocation = itemView.findViewById(R.id.thirdLocation);
        }

    }
}
