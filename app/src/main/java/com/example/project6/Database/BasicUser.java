package com.example.project6.Database;

import java.util.Date;

public abstract class BasicUser {

    // personal info
    private String givenName;
    private String familyName;
    private String previousName;
    private Date dateOfBirth;
    private Sex sex;
    private boolean aboriginalTorresStraight;
    private boolean australianCitizen;
    private String nationality;

    // contact info
    private String email;
    private String phoneNo;

    public BasicUser() {
    }

    public BasicUser(String givenName, String familyName, String previousName, Date dateOfBirth, Sex sex, boolean aboriginalTorresStraight, boolean australianCitizen, String nationality, String email, String phoneNo) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.previousName = previousName;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.aboriginalTorresStraight = aboriginalTorresStraight;
        this.australianCitizen = australianCitizen;
        this.nationality = nationality;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPreviousName() {
        return previousName;
    }

    public void setPreviousName(String previousName) {
        this.previousName = previousName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public boolean isAboriginalTorresStraight() {
        return aboriginalTorresStraight;
    }

    public void setAboriginalTorresStraight(boolean aboriginalTorresStraight) {
        this.aboriginalTorresStraight = aboriginalTorresStraight;
    }

    public boolean isAustralianCitizen() {
        return australianCitizen;
    }

    public void setAustralianCitizen(boolean australianCitizen) {
        this.australianCitizen = australianCitizen;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}