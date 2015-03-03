package com.itgarage.harvey.gamecollections.models;

/**
 * Created by harvey on 2015-03-03.
 */
public class User {
    private String username;
    private String profileID;
    private String fb_or_google;

    public User(String username, String profileID, String fb_or_google) {
        this.username = username;
        this.profileID = profileID;
        this.fb_or_google = fb_or_google;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public String getFb_or_google() {
        return fb_or_google;
    }

    public void setFb_or_google(String fb_or_google) {
        this.fb_or_google = fb_or_google;
    }
}
