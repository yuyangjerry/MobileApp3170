package com.example.project6.Database;

import com.google.firebase.Timestamp;

import java.util.List;

public class Reading {

    String patientId;
    Timestamp timestamp;
    List<Integer> data;

    public Reading() {
    }

    public Reading(String patientId, Timestamp timestamp, List<Integer> data) {
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.data = data;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }
}
