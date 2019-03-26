package com.project.smartbus10;



public class StudentTimeNote {
    private String notID;
    private Student student;
    private String date;
    private String EnterTime;
    private String leaveTime;
    private String state;

    public void setNotID(String notID) {
        this.notID = notID;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEnterTime(String enterTime) {
        EnterTime = enterTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNotID() {
        return notID;
    }

    public Student getStudent() {
        return student;
    }

    public String getDate() {
        return date;
    }

    public String getEnterTime() {
        return EnterTime;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return   "Date " + date + ' ' + "EnterTime " + EnterTime + ' ' + "leaveTime " + leaveTime + ' ' ;
    }
}
