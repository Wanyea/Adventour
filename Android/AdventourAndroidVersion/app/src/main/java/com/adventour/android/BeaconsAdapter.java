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


public class BeaconsAdapter extends RecyclerView.Adapter<BeaconsAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<BeaconsModel> beaconsArrayList;

    public BeaconsAdapter(Context context, ArrayList<BeaconsModel> beaconsArrayList) {
        this.context = context;
        this.beaconsArrayList = beaconsArrayList;
    }

    @NonNull
    @Override
    public BeaconsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconsAdapter.ViewHolder holder, int position) {
        BeaconsModel model = beaconsArrayList.get(position);

        holder.beaconTitle.setText(model.getBeaconTitle());
        holder.beaconIntro.setText(model.getBeaconIntro());
        holder.beaconAuthor.setText(model.getBeaconAuthor());
        holder.beaconImage.setImageBitmap(model.getBeaconBitmap());
        holder.beaconCreatedDate.setText(model.getDateCreated());

        switch (model.getAndroidPfpRef())
        {
            // Set profile pic image to Cheetah
            case 2131230902:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_cheetah);
                    break;

            // Set profile pic image to Elephant
            case 2131230903:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_elephant);
                    break;

            // Set profile pic image to Ladybug
            case 2131230905:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_ladybug);
                    break;

            // Set profile pic image to Monkey
            case 2131230906:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_monkey);
                    break;

            // Set profile pic image to Fox
            case 2131230904:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_fox);
                    break;

            // Set profile pic image to Penguin
            case 2131230907:
                holder.authorImageView.setImageResource(R.drawable.ic_profpic_penguin);
                    break;
        }
    }

    @Override
    public int getItemCount() {
        return beaconsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView beaconTitle;
        private final TextView beaconIntro;
        private final TextView beaconAuthor;
        private final ImageView beaconImage;
        private final ImageView authorImageView;
        private final TextView beaconCreatedDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            beaconTitle = itemView.findViewById(R.id.beaconTitleTextView);
            beaconIntro = itemView.findViewById(R.id.beaconDescriptionTextView);
            beaconAuthor = itemView.findViewById(R.id.authorTextView);
            beaconImage = itemView.findViewById(R.id.locationImageView);
            beaconCreatedDate = itemView.findViewById(R.id.beaconDate);
            authorImageView = itemView.findViewById(R.id.authorImageView);
        }

    }
}
