package com.project.smartbus10;

public class Item {
    private String ID;
    private String FirstName;
    private String LastName;
    private int profileImage;
    private String phone;
    private String plate;
    private String description;

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getID() {
        return ID;
    }

    public String getPlate() {
        return plate;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }
}
