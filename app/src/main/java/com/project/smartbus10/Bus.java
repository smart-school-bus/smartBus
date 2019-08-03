package com.project.smartbus10;

import java.io.Serializable;
import java.util.List;

public class Bus implements Serializable {
    private String ID;
    private double busLatitude;
    private double busLongitude;
    private int studentsNumber;
    private String plate;
    private SchoolAdministration admin;
    private Driver driver;
    private List<BusStop> busStopList;
    private List<Student> studentList;

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setBusLatitude(double busLatitude) {
        this.busLatitude = busLatitude;
    }

    public void setBusLongitude(double busLongitude) {
        this.busLongitude = busLongitude;
    }

    public void setStudentsNumber(int studentsNumber) {
        this.studentsNumber = studentsNumber;
    }

    public void setPlate(String plate) { this.plate = plate; }

    public void setAdmin(SchoolAdministration admin) { this.admin = admin; }

    public void setDriver(Driver driver) { this.driver = driver; }

    public void addBusStopL(BusStop busStop){busStopList.add(busStop);}

    public void addStudent(Student student){studentList.add(student);}

    public String getID() { return ID; }

    public double getBusLatitude() {
        return busLatitude;
    }

    public double getBusLongitude() {
        return busLongitude;
    }

    public int getStudentsNumber() {
        return studentsNumber;
    }

    public String getPlate() { return plate; }

    public SchoolAdministration getAdmin() { return admin; }

    public Driver getDriver() { return driver; }

    public List<BusStop>getBusStopList(){ return busStopList; }

    public List<Student>getStudentList(){ return studentList; }
    @Override
    public String toString() { return ID; }
}
