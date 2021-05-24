package com.example.project6.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Patient extends BasicUser{

    // personal info
    private String bloodType;
    private double weight;
    private double height;
    private int stdDrinksPerWeek;
    private double packYear;

    // flag info
    private List<String> flags;

    public Patient() {
    }

    public Patient(String givenName, String familyName, String previousName, Date dateOfBirth, Sex sex, boolean aboriginalTorresStraight, boolean australianCitizen, String nationality, String email, String phoneNo, String bloodType, double weight, double height, int stdDrinksPerWeek, int cigsPerDay, int yearsSmoking) {
        super(givenName, familyName, previousName, dateOfBirth, sex, aboriginalTorresStraight, australianCitizen, nationality, email, phoneNo);
        this.bloodType = bloodType;
        this.weight = weight;
        this.height = height;
        this.stdDrinksPerWeek = stdDrinksPerWeek;
        this.packYear = calcPackYear(cigsPerDay, yearsSmoking);
        this.flags = new ArrayList<>();
    }

    // pack year calc according to  https://en.wikipedia.org/wiki/Pack-year
    private double calcPackYear(double cigsPerDay, double yearsSmoking) {
        System.out.println(cigsPerDay + "     nsakjbdkjsbkjd " + yearsSmoking );
        return (cigsPerDay/20) * yearsSmoking;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getStdDrinksPerWeek() {
        return stdDrinksPerWeek;
    }

    public void setStdDrinksPerWeek(int stdDrinksPerWeek) {
        this.stdDrinksPerWeek = stdDrinksPerWeek;
    }

    public double getPackYear() {
        return packYear;
    }

    public void setPackYear(double packYear) {
        this.packYear = packYear;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }
}