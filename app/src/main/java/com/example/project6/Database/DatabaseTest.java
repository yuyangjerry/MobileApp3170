package com.example.project6.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTest {


    public void testAddNewPatient() throws ParseException {

        // fill in with random person generator

        // personal info
        String givenName = "Patrick";
        String familyName = "Barwell";
        String previousName = "";

        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);



        Date dateOfBirth = format.parse("07/02/1965");


        Sex sex = Sex.MALE;
        boolean aboriginalTorresStraight = false;
        boolean australianCitizen = true;
        String nationality = "Australian";

        // contact info
        String email = "PatrickBarwell@teleworm.us";
        String phoneNo = "0412345678";

        // patient info

        // personal info
        String bloodType = "A-";
        double weight = 94.4;
        int height = 187;
        int stdDrinksPerWeek = 3;
        int cigsPerDay = 2;
        int yearSmoking = 20;


        // create patient object


        Patient newPatient =  new Patient(givenName,  familyName,  previousName,  dateOfBirth,  sex,  aboriginalTorresStraight,  australianCitizen,  nationality,  email,  phoneNo,  bloodType,  weight,  height,  stdDrinksPerWeek, cigsPerDay, yearSmoking);

        DatabaseCommunication database = new FirebaseCommunication();

        database.createNewPatient(newPatient);
    }

    public void testAddReading(){

        // copied from firebase online
        String patientId = "RuDI7eTIwhFKLSgaM7FL";

        // make up some data points
        List<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(2);
        data.add(3);
        data.add(4);

        // generate timestamp for now
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date now = new Date(System.currentTimeMillis());
        Timestamp timestamp = new Timestamp(now);

        Reading newReading = new Reading(patientId, timestamp, data);

        DatabaseCommunication database = new FirebaseCommunication();

        database.postReading(newReading);


    }
}
