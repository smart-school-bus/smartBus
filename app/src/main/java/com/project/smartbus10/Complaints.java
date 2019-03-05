package com.project.smartbus10;

public class Complaints {
    private String ComplaintsID;
    private String Description;
    private Parent parent;
    private Driver driver;

    public void setParent(Parent parent) { this.parent = parent; }

    public void setDriver(Driver driver) { this.driver = driver; }

    public void setComplaintsID(String complaintsID) {
        ComplaintsID = complaintsID;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getComplaintsID() {
        return ComplaintsID;
    }

    public String getDescription() {
        return Description;
    }

    public Parent getParent() { return parent; }

    public Driver getDriver() { return driver; }
}
