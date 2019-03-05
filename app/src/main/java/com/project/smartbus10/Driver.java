package com.project.smartbus10;

public class Driver {
    private String DriverID;
    private String UserName;
    private String FirstName;
    private String SecondName;
    private String LastName;
    private String Phone;
    private String Password;

    public void setDriverID(String driverID) {
        DriverID = driverID;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }


    public void setLastName(String lastName) { LastName = lastName; }

    public void setSecondName(String secondName) {
        SecondName = secondName;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDriverID() {
        return DriverID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getSecondName() {
        return SecondName;
    }

    public String getLastName() { return LastName; }

    public String getPhone() {
        return Phone;
    }

    public String getPassword() {
        return Password;
    }
}
