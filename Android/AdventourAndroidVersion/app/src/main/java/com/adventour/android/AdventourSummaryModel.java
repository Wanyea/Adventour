package com.adventour.android;

import android.graphics.Bitmap;

public class AdventourSummaryModel {
    private String name;
    private String description;
    private Bitmap image;

    public AdventourSummaryModel (String name, String description, Bitmap image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public AdventourSummaryModel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
