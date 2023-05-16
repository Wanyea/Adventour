package com.adventour.android;

import java.util.Date;

public class Photo {

    private int id;
    private String localURI;
    private String remoteURI;
    private String title;
    private String description;
    private String dateTaken;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocalURI() {
        return localURI;
    }

    public void setLocalURI(String localURI) {
        this.localURI = localURI;
    }

    public String getRemoteURI() {
        return remoteURI;
    }

    public void setRemoteURI(String remoteURI) {
        this.remoteURI = remoteURI;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }
}
