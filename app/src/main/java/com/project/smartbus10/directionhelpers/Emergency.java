package com.project.smartbus10.directionhelpers;

import com.project.smartbus10.Driver;
import com.project.smartbus10.SchoolAdministration;

public class Emergency {
    private String emergency_id;
    private Driver driver;
    private SchoolAdministration schoolAdministration;
    private String message;

    public void setEmergency_id(String emergency_id) {
        this.emergency_id = emergency_id;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setSchoolAdministration(SchoolAdministration schoolAdministration) {
        this.schoolAdministration = schoolAdministration;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmergency_id() {
        return emergency_id;
    }

    public Driver getDriver() {
        return driver;
    }

    public SchoolAdministration getSchoolAdministration() {
        return schoolAdministration;
    }

    public String getMessage() {
        return message;
    }
}
