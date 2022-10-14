package com.adventour.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class BeaconPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_post);

        RecyclerView beaconPostRV = findViewById(R.id.beaconPostRV);
        beaconPostRV.setNestedScrollingEnabled(true);

        ArrayList<BeaconPostModel> BeaconPostModelArrayList = new ArrayList<BeaconPostModel>();
        BeaconPostModelArrayList.add(new BeaconPostModel("University of Central Florida", "1", "1", "123 Hello", "dfjkhbdfjdfjkdffldjkhdfkljhdfkjlhdfjkhdfgjkhdfgjdfg"));
        BeaconPostModelArrayList.add(new BeaconPostModel("The Cloak & Blaster", "2", "2", "456 AAaaAAaaAa", "oh"));
        BeaconPostModelArrayList.add(new BeaconPostModel("American Escape Rooms Orlando", "3", "3", "789 bRUH", "let's fucking goooooooo"));
        BeaconPostModelArrayList.add(new BeaconPostModel("Arcade Monsters", "4","4", "1011 help", "it sucked ass"));
        BeaconPostModelArrayList.add(new BeaconPostModel("Congo River Golf", "4", "5", "69 Nice", "ahhahahahahahahahaha"));

        BeaconPostAdapter BeaconPostAdapter = new BeaconPostAdapter(this, BeaconPostModelArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        beaconPostRV.setLayoutManager(linearLayoutManager);
        beaconPostRV.setAdapter(BeaconPostAdapter);
    }
}