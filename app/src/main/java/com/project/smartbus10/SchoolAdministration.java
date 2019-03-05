package com.project.smartbus10;

public class SchoolAdministration {

    private String AdminID;
    private String UserName;
    private String FirstName;
    private String SecondName;
    private String LastName;
    private String Phone;
    private String Password;

    public void setAdminID(String adminID) {
        AdminID = adminID;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public void setSecondName(String secondName) { SecondName = secondName; }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setPassword(String password) { Password = password; }

    public String getAdminID() {
        return AdminID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getSecondName() { return SecondName; }

    public String getLastName() {
        return LastName;
    }

    public String getPhone() {
        return Phone;
    }

    public String getPassword() {
        return Password;
    }
}
