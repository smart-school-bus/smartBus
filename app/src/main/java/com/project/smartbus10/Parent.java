package com.project.smartbus10;

import android.net.Uri;

import java.io.Serializable;

public class Parent implements Serializable {
private String PatentID;
    private String UserName;
    private String Password;
    private int Phone;
    private String FirstName;
    private String LastName;
    private float Latitude;
    private float Longitude;//نتأكد منها
    private Uri profileImage;
    //student list

    public void setPatentID(String patentID) {
        PatentID = patentID;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setPhone(int phone) {
        Phone = phone;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setLatitude(float latitude) {
        Latitude = latitude;
    }

    public void setLongitude(float longitude) {
        Longitude = longitude;
    }

    public void setProfileImage(Uri profileImage) { this.profileImage = profileImage; }

    public String getPatentID() { return PatentID; }

    public String getUserName() {
        return UserName;
    }

    public String getPassword() {
        return Password;
    }

    public int getPhone() {
        return Phone;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public float getLatitude() {
        return Latitude;
    }

    public float getLongitude() { return Longitude; }

    public Uri getProfileImage() { return profileImage; }
}
