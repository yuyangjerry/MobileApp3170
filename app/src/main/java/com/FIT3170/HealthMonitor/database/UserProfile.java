package com.FIT3170.HealthMonitor.database;

import com.FIT3170.HealthMonitor.FireBaseAuthClient;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * This is a Singleton class used to hold the document
 * stored at /patients/:patientId
 *
 * This class is singleton because you would expect only one user logged
 * in through the app at a time;
 */
public class UserProfile {
    private static UserProfile instance = null;
    private FirebaseFirestore db;
    private String uid;
    private DocumentSnapshot profile;
    private UserProfile(){
        db = FirebaseFirestore.getInstance();
        uid = FireBaseAuthClient.getSignedInUser().getUid();
    }

    private void downloadProfile(Callback<Boolean, Exception> onDownload){
        db
                .collection("patients")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       profile = task.getResult();
                       onDownload.onCall(true, null);
                   }else{
                        Exception e = task.getException();
                        onDownload.onCall(null, e);
                   }
                });
    }

    /**
     * Fetch the user profile from firestore
     * @param onFetch is called with a true value if we where able to download
     *               the use profile
     */
    static public void fetch(Callback<Boolean, Exception> onFetch){
        if(!isFetched()){
            UserProfile.instance = new UserProfile();
            UserProfile.instance.downloadProfile(onFetch);
        }else{
            onFetch.onCall(null, new Exception("Profile already fetched"));
        }
    }

    /**
     * Push the user profile changes to the database
     */
    static public void sync(){
        if(isFetched()){
            //TODO: store the data
//            instance.db
//                    .collection("patients")
//                    .document(instance.uid)
//                    .set()
        }else{

        }
    }

    /**
     *
     * @return true if the profile has been fetched, false otherwise
     */
    static public boolean isFetched(){
        return UserProfile.instance != null && UserProfile.instance.profile != null;
    }

    static public String getUid() {
        return instance.uid;
    }

    static public String getGivenName() {
        return instance.profile.getString("givenName");
    }

    static public String getBloodType(){
        return instance.profile.getString("bloodType");
    }

    static public String getWeight(){
        return instance.profile.getString("weight");
    }

    static public String getHeight(){
        return instance.profile.getString("height");
    }

    static public Timestamp getDateOfBirth(){
        return instance.profile.getTimestamp("dateOfBirth");
    }

    static public List<String> getLinkedDoctorIds(){
        List<String> doctorIds = (List<String>) instance.profile.get("doctors");
        return doctorIds;
    }

    static public String getFamilyName() {
        //TODO: surname is not the correct fieldname,
        // it should be changed in the database into familyName
        return instance.profile.getString("surname");
    }

    static public String getEmail(){
        return instance.profile.getString("email");
    }

    static public String getGender(){
        return instance.profile.getString("gender");
    }

    static public String getMaritalStatus(){
        return instance.profile.getString("maritalStatus");
    }

    static public String getPhone(){
        return instance.profile.getString("phone")
    }

}