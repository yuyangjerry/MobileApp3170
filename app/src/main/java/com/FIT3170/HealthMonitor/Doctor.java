package com.FIT3170.HealthMonitor;

public class Doctor {
    private String doctorID;
    private String doctorGivenName;
    private String doctorFamilyName;
    private String doctorEmail;

    public String getDoctorFamilyName() {
        return doctorFamilyName;
    }

    public void setDoctorFamilyName(String doctorFamilyName) {
        this.doctorFamilyName = doctorFamilyName;
    }


    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public String getDoctorGivenName() {
        return doctorGivenName;
    }

    public void setDoctorGivenName(String doctorGivenName) {
        this.doctorGivenName = doctorGivenName;
    }

    public Doctor(String doctorID, String doctorGivenName, String doctorFamilyName, String doctorEmail) {
        this.doctorGivenName = doctorGivenName;
        this.doctorID = doctorID;
        this.doctorEmail = doctorEmail;
        this.doctorFamilyName = doctorFamilyName;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }
}
