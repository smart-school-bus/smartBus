package com.project.smartbus10;

public class Student {
    private String StuID;
    private String StuFirstName;
    private String StuLastName;
    private String StuAttendance;
    //private String BusID;
   // private String BusStopID;


    public void setStuID(String stuID) {
        StuID = stuID;
    }

    public void setStuFirstName(String stuFirstName) {
        StuFirstName = stuFirstName;
    }

    public void setStuLastName(String stuLastName) {
        StuLastName = stuLastName;
    }

    public void setStuAttendance(String stuAttendance) {
        StuAttendance = stuAttendance;
    }

    public String getStuID() {
        return StuID;
    }

    public String getStuFirstName() {
        return StuFirstName;
    }

    public String getStuLastName() {
        return StuLastName;
    }

    public String getStuAttendance() {
        return StuAttendance;
    }
}
