package com.example.project6.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Doctor extends BasicUser{
    private List<String> placesOfPractice;
    private List<String> linkedPatients;

    public Doctor() {
    }

    public Doctor(String givenName, String familyName, String previousName, Date dateOfBirth, Sex sex, boolean aboriginalTorresStraight, boolean australianCitizen, String nationality, String email, String phoneNo, List<String> placesOfPractice) {
        super(givenName, familyName, previousName, dateOfBirth, sex, aboriginalTorresStraight, australianCitizen, nationality, email, phoneNo);
        this.placesOfPractice = placesOfPractice;
        this.linkedPatients = new ArrayList<>();
    }

    public List<String> getPlacesOfPractice() {
        return placesOfPractice;
    }

    public void setPlacesOfPractice(List<String> placesOfPractice) {
        this.placesOfPractice = placesOfPractice;
    }

    public List<String> getLinkedPatients() {
        return linkedPatients;
    }

    public void setLinkedPatients(List<String> linkedPatients) {
        this.linkedPatients = linkedPatients;
    }
}