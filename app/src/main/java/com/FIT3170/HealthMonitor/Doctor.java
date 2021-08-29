package com.FIT3170.HealthMonitor;

public class Doctor {
    private String doctorid, doctorname;

    public String getDoctorname() {
        return doctorname;
    }

    public void setDoctorname(String doctorname) {
        this.doctorname = doctorname;
    }

    public Doctor(String doctorid, String doctorname) {
        this.doctorname = doctorname;
        this.doctorid = doctorid;
    }

    public String getDoctorid() {
        return doctorid;
    }

    public void setDoctorid(String doctorid) {
        this.doctorid = doctorid;
    }
}
