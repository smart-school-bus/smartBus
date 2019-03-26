package com.project.smartbus10;

import java.io.Serializable;

public class SchoolAdministration  implements Serializable {

    private String AdminID;
    private String firstName;
    private String secondName;
    private String lastName;
    private String phone;
    private String password;

    public void setAdminID(String adminID) {
        AdminID = adminID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) { this.secondName = secondName; }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) { this.password = password; }

    public String getAdminID() {
        return AdminID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() { return secondName; }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}
