package com.project.smartbus10;

public class Bus {
    private String StuID;
  //  private String AdminID;
    private String BusLatitude;
    private String BusLongitude;
    private String StudentNumber;
    //bus stop


    public void setStuID(String stuID) {
        StuID = stuID;
    }

    public void setBusLatitude(String busLatitude) {
        BusLatitude = busLatitude;
    }

    public void setBusLongitude(String busLongitude) {
        BusLongitude = busLongitude;
    }

    public void setStudentNumber(String studentNumber) {
        StudentNumber = studentNumber;
    }

    public String getStuID() {
        return StuID;
    }

    public String getBusLatitude() {
        return BusLatitude;
    }

    public String getBusLongitude() {
        return BusLongitude;
    }

    public String getStudentNumber() {
        return StudentNumber;
    }
}
