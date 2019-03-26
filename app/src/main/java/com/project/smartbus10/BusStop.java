package com.project.smartbus10;

import java.io.Serializable;
import java.util.List;

public class BusStop implements Serializable {

    private String busStopID;
    private String BusStopName;
    private int busStopOrder;
    private double busStopLatitude;
    private double busStopLongitude;
    private List<Student> studentList;
    private Bus bus;

    public void setBusStopID(String busStopID) {
        this.busStopID = busStopID;
    }

    public void setBusStopName(String busStopName) {
        BusStopName = busStopName;
    }

    public void setBusStopOrder(int busStopOrder) {
        this.busStopOrder = busStopOrder;
    }

    public void setBusStopLatitude(double busStopLatitude) { this.busStopLatitude = busStopLatitude; }

    public void setBusStopLongitude(double busStopLongitude) { this.busStopLongitude = busStopLongitude; }

    public void setStudentList(List<Student> studentList) { this.studentList = studentList; }

    public String getBusStopID() {
        return busStopID;
    }

    public String getBusStopName() {
        return BusStopName;
    }

    public int getBusStopOrder() {
        return busStopOrder;
    }

    public double getBusStopLatitude() {
        return busStopLatitude;
    }

    public double getBusStopLongitude() {
        return busStopLongitude;
    }

    public List<Student> getStudentList() { return studentList; }

    @Override
    public String toString() { return busStopID; }
}
