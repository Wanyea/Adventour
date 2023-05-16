package com.adventour.android;

import java.util.Date;

public class Adventourist {

    private int id;
    private String email;
    private String mobile;
    private String nickname;
    private String firstName;
    private String middleName;
    private String lastName;
    private String bio;
    private String mantra;
    private String birthdate;
    private String dateJoined;
    private String dateLastLogin;
    private String dateLastAdventour;
    private boolean isPrivate;
    // An array of Adventour ids
    private int[] favorites;

    // These may be unused, time will tell
    private Adventour[] adventours;
    private Photo[] profilePictures;
    private NeverRecommend[] nevers;

    public Adventourist() {
        this.id = 0;
        this.email = "";
        this.mobile = "";
        this.nickname = "";
        this.firstName = "";
        this.middleName = "";
        this.lastName = "";
        this.bio = "";
        this.mantra = "";
        this.birthdate = null;
        this.dateJoined = null;
        this.dateLastLogin = null;
        this.dateLastAdventour = null;
        this.isPrivate = true;
    }


    public Adventourist(String email, String nickname, String birthdate, String dateJoined, boolean isPrivate) {
        this.email = email;
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.dateJoined = dateJoined;
        this.isPrivate = isPrivate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getMantra() {
        return mantra;
    }

    public void setMantra(String mantra) {
        this.mantra = mantra;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getDateLastLogin() {
        return dateLastLogin;
    }

    public void setDateLastLogin(String dateLastLogin) {
        this.dateLastLogin = dateLastLogin;
    }

    public String getDateLastAdventour() {
        return dateLastAdventour;
    }

    public void setDateLastAdventour(String dateLastAdventour) {
        this.dateLastAdventour = dateLastAdventour;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
