package com.project.smartbus10;

import java.io.Serializable;

public class Student  implements Serializable {
    private String StuID;
    private String firstName;
    private String lastName;
    private boolean stuAttendance;
    private String tag;
    private String level ;
    private Parent parent;
    private Bus bus;
    private BusStop busStop;
    private String date;
    private String EnterTime;
    private String leaveTime;
    private String state;



    public void setStuID(String stuID) { StuID = stuID; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setStuAttendance(boolean stuAttendance) { this.stuAttendance = stuAttendance; }

    public void setLevel(String level) { this.level = level; }

    public void setTag(String tag) { this.tag = tag; }

    public void setParent(Parent parent) { this.parent = parent; }

    public void setBus(Bus bus) { this.bus = bus; }

    public void setBusStop(BusStop busStop) { this.busStop = busStop; }

    public void setDate(String date) { this.date = date; }

    public void setEnterTime(String enterTime) { EnterTime = enterTime; }

    public void setLeaveTime(String leaveTime) { this.leaveTime = leaveTime; }

    public void setState(String state) { this.state = state; }

    public String getStuID() { return StuID; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public boolean getStuAttendance() { return stuAttendance; }

    public String getLevel() { return level; }

    public String getTag() { return tag; }

    public Parent getParent() { return parent; }

    public Bus getBus() { return bus ;}

    public BusStop getBusStop() { return busStop; }

    public String getDate() { return date; }

    public String getEnterTime() { return EnterTime; }

    public String getLeaveTime() { return leaveTime; }

    public String getState() { return state; }
    @Override
    public String toString() {
        return   "Date " + date + ' ' + "EnterTime " + EnterTime + ' ' + "leaveTime " + leaveTime + ' ' ;
    }
}
