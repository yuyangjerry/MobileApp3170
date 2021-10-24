package com.FIT3170.HealthMonitor;

import java.sql.Timestamp;

//doctor class, containing all doctor attribute and method to get doctor information
public class Doctor {
    private String doctorID;
    private String doctorGivenName;
    private String doctorFamilyName;
    private String doctorEmail;
    private String phoneNumber;
    private String PlaceOfPractice;

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

    public Doctor(String doctorID, String doctorGivenName, String doctorFamilyName, String doctorEmail, String phoneNumber, String PlaceOfPractice) {
        this.doctorGivenName = doctorGivenName;
        this.doctorID = doctorID;
        this.doctorEmail = doctorEmail;
        this.doctorFamilyName = doctorFamilyName;
        this.PlaceOfPractice = PlaceOfPractice;
        this.phoneNumber = phoneNumber;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPlaceOfPractice() {
        return PlaceOfPractice;
    }

    public void setPlaceOfPractice(String PlaceOfPractice) {
        this.PlaceOfPractice = PlaceOfPractice;
    }

}
