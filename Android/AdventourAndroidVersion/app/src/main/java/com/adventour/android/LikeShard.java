package com.adventour.android;

import java.io.Serializable;

public class LikeShard implements Serializable
{
    int likeCount;

    public LikeShard(int likeCount) {
        this.likeCount = likeCount;
    }
}
