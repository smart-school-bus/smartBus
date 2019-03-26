package com.project.smartbus10;

import java.io.Serializable;
import java.util.List;

public class Parent implements Serializable {

    private String PatentID;
    private String password;
    private String phone;
    private String firstName;
    private String secondName;
    private String lastName;
    private double latitude;
    private double longitude;
    private String address;
    private List<Student> students;

    public void setPatentID(String patentID) {
        PatentID = patentID;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) { this.secondName = secondName; }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) { this.address = address; }

    public void addStudent(Student student){students.add(student);}

    public String getPatentID() { return PatentID; }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {return secondName; }

    public String getLastName() {
        return lastName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() { return longitude; }

    public String getAddress() { return address; }

    public List<Student> getStudents(){return students;};

    @Override
    public String toString() { return PatentID ; }
}
