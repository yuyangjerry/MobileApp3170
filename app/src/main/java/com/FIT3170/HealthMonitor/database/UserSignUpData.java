package com.FIT3170.HealthMonitor.database;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class UserSignUpData {

    private String givenName;
    private String familyName;
    private String bloodType;
    private String weight;
    private String height;
    private Timestamp dateOfBirth;
    private String email;
    private String gender;
    private String maritalStatus;
    private String phone;

    public String getGivenName() {
        return givenName;
    }

    public UserSignUpData setGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public String getFamilyName() {
        return familyName;
    }

    public UserSignUpData setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    public String getBloodType() {
        return bloodType;
    }

    public UserSignUpData setBloodType(String bloodType) {
        this.bloodType = bloodType;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public UserSignUpData setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public String getHeight() {
        return height;
    }

    public UserSignUpData setHeight(String height) {
        this.height = height;
        return this;
    }

    public Timestamp getDateOfBirth() {
        return dateOfBirth;
    }

    public UserSignUpData setDateOfBirth(Timestamp dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserSignUpData setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public UserSignUpData setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public UserSignUpData setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserSignUpData setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
