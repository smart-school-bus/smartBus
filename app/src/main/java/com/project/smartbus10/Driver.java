package com.project.smartbus10;

import java.io.Serializable;

public class Driver  implements Serializable {
    private String DriverID;
    private String firstName;
    private String secondName;
    private String lastName;
    private String phone;
    private String password;
    private Bus bus;

    public void setDriverID(String driverID) {
        this.DriverID = driverID;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBus(Bus bus) { this.bus = bus; }

    public String getDriverID() {
        return DriverID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getLastName() { return lastName; }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public Bus getBus() { return bus; }

    @Override
    public String toString() {
        return "\n DriverID :" + DriverID +
                "\n firstName :" + firstName +
                "\n secondName :" + secondName +
                "\n lastName :" + lastName +
                "\n phone :" + phone ;
    }
}
