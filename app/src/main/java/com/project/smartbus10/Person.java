package com.project.smartbus10;

import android.net.Uri;

public class Person {
    private String ID;
    private String FirstName;
    private String LastName;
    private Uri profileImage;
    private String phone;

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String name) {
        this.FirstName = name;
    }

    public Uri getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Uri profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
