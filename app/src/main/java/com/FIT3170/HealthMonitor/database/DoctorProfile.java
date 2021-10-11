package com.FIT3170.HealthMonitor.database;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/**
 * Retrieve doctor profile by doctor id
 */
public class DoctorProfile {

    private Map<String, Object> doctor;
    private String uid;

    /**
     * Fetch a doctor profile by id
     * @param uid doctor id
     * @param onFetch callback triggered after the doctor profile arrives
     */
    public DoctorProfile(String uid, Callback<Boolean, Exception> onFetch){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("doctors")
                .document(uid)
                .get()
                .addOnCompleteListener( l -> {
                    if(l.isSuccessful()){
                        doctor = l.getResult().getData();
                        this.uid = uid;
                        onFetch.onCall(true, null);
                    }else{
                        onFetch.onCall(null, l.getException());
                    }
                });
    }

    public String getUid(){
        return uid;
    }

    public String getEmail(){
        return (String)doctor.get("email");
    }

    public String getGivenName(){
        return (String)doctor.get("givenName");
    }

    public  String getFamilyName(){
        return (String)doctor.get("familyName");
    }

    public Timestamp getDateOfBirth(){
        Object o = doctor.get("dob");
        return o == null ? null : (Timestamp)o;
    }

    public String getPhoneNumber(){ return (String)doctor.get("phone"); }

    public String getPlaceOfPractice() { return (String)doctor.get("placeOfPractice"); }



}
