package com.adventour.android;

public class Location
{
    private String fsq_id;
    private String name;
    private String description;
    private String tel;
    private String website;
    private String rating;

    public Location(String fsq_id, String name, String description, String tel, String website, String rating) {
        this.fsq_id = fsq_id;
        this.name = name;
        this.description = description;
        this.tel = tel;
        this.website = website;
        this.rating = rating;
    }

    public String getFsq_id() {
        return fsq_id;
    }

    public void setFsq_id(String fsq_id) {
        this.fsq_id = fsq_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
