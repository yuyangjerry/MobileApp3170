package com.FIT3170.HealthMonitor.database;

/**
 * This is a Singleton class used to hold the document
 * stored at /patients/:patientId
 */
public class UserProfile {

    private static UserProfile instance = null;

    private UserProfile(){
        //TODO: get the user profile
    }

    /**
     * Fetch the user profile from firestore
     */
    static public void init(){
        if(UserProfile.instance == null){
            UserProfile.instance = new UserProfile();
        }
    }
}
