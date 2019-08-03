package com.project.smartbus10;

import java.io.Serializable;
import java.util.List;

public class BusStop implements Serializable , Comparable {

    private String busStopID;
    private String busStopAddress;
    private String pathAudio;
    private String linkAudio;
    private int busStopOrder;
    private double busStopLatitude;
    private double busStopLongitude;
    private List<Student> studentList;
    private Bus bus;


    public void setBusStopID(String busStopID) {
        this.busStopID = busStopID;
    }

    public void setBusStopAddress(String busStopAddress) {
        this.busStopAddress = busStopAddress;
    }

    public void setBusStopOrder(int busStopOrder) {
        this.busStopOrder = busStopOrder;
    }

    public void setPathAudio(String pathAudio) { this.pathAudio = pathAudio; }

    public void setLinkAudio(String linkAudio) { this.linkAudio = linkAudio; }

    public void setBus(Bus bus) { this.bus = bus; }

    public void setBusStopLatitude(double busStopLatitude) { this.busStopLatitude = busStopLatitude; }

    public void setBusStopLongitude(double busStopLongitude) { this.busStopLongitude = busStopLongitude; }

    public void setStudentList(Student student) { studentList.add(student);}

    public String getBusStopID() {
        return busStopID;
    }

    public String getBusStopAddress() {
        return busStopAddress;
    }

    public int getBusStopOrder() {
        return busStopOrder;
    }

    public String getPathAudio() { return pathAudio; }

    public String getLinkAudio() { return linkAudio; }

    public Bus getBus() { return bus; }

    public double getBusStopLatitude() {
        return busStopLatitude;
    }

    public double getBusStopLongitude() {
        return busStopLongitude;
    }

    public List<Student> getStudentList() { return studentList; }

    @Override
    public String toString() { return busStopID; }


    @Override
    public int compareTo(Object busStop) {
        int busStopOrder = ((BusStop) busStop).getBusStopOrder();

        //ascending order
        return this.busStopOrder - busStopOrder;

        //descending order
        //return busStopOrder - this.busStopOrder;
    }
}
