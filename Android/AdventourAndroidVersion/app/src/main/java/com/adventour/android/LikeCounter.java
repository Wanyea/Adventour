package com.adventour.android;

import java.io.Serializable;

public class LikeCounter implements Serializable
{
    int numLikeShards;

    public LikeCounter(int numLikeShards) {
        this.numLikeShards = numLikeShards;
    }
}
