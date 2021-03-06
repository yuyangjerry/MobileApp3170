package com.FIT3170.HealthMonitor.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.FIT3170.HealthMonitor.FireBaseAuthClient;
import com.FIT3170.HealthMonitor.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Time;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a Singleton class used to hold the document
 * stored at /patients/:patientId
 *
 * This class is singleton because you would expect only one user logged
 * in through the app at a time;
 *
 * Manual:
 *  1. Call UserProfile.fetch() when the user signs in
 *  2. Wait until the callback is called or UserProfile.isFetched() returns true
 *  3. Access the values you need using the setter methods
 *  4. Modify the values you want to modify using the getters method
 *  5. If you want to revert some changes call the UserProfile.revertChanges()
 *  6. Call UserProfile.save() to save the modified profile in the database
 */
public class UserProfile {

    public static final String GIVEN_NAME_KEY = "givenName";
    public static final String FAMILY_NAME_KEY = "familyName";
    public static final String BLOOD_TYPE_KEY = "bloodType";
    public static final String WEIGHT_KEY = "weight";
    public static final String HEIGHT_KEY = "height";
    public static final String DATE_OF_BIRTH_KEY = "dateOfBirth";
    public static final String DOCTORS_KEY = "doctors";
    public static final String EMAIL_KEY = "email";
    public static final String GENDER_KEY = "gender";
    public static final String MARITAL_STATUS_KEY = "maritalStatus";
    public static final String PHONE_KEY = "phone";
    public static final String NOTIFICATION_TITLE = "notificationTitle";
    public static final String NOTIFICATION_DESCRIPTION = "notificationDescription";
    public static final String NOTIFICATION_TIME = "notificationTime";

    private static UserProfile instance = null;
    private FirebaseFirestore db;
    private String uid;

    /**
     * Hold a modifiable copy of the profile
     */
    private Map<String, Object> modifiableProfile;

    /**
     * Hold an unmodifiable copy of the profile in case we need to revert changes
     */
    private Map<String, Object> backupProfile;


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
                       DocumentSnapshot snap = task.getResult();
                       backupProfile = snap.getData();
                       modifiableProfile = MapDeepCopier.deepCopy(backupProfile);
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
        instance = new UserProfile();
        instance.downloadProfile(onFetch);

    }

    /**
     * Create a new user in Firestore
     * @param data the user profile data
     * @param onCreate a callback
     */
    static public void create(UserSignUpData data, Callback<Boolean, Exception> onCreate){
        instance = new UserProfile();
        instance.backupProfile = new HashMap<String, Object>();

        MapFiller filler = new MapFiller(instance.backupProfile);

        filler.putIfNotNull(GIVEN_NAME_KEY, data.getGivenName());
        filler.putIfNotNull(FAMILY_NAME_KEY, data.getFamilyName());
        filler.putIfNotNull(BLOOD_TYPE_KEY, data.getBloodType());
        filler.putIfNotNull(WEIGHT_KEY, data.getWeight());
        filler.putIfNotNull(HEIGHT_KEY, data.getHeight());
        filler.putIfNotNull(DATE_OF_BIRTH_KEY, data.getDateOfBirth());
        filler.putIfNotNull(EMAIL_KEY, data.getEmail());
        filler.putIfNotNull(GENDER_KEY, data.getGender());
        filler.putIfNotNull(MARITAL_STATUS_KEY, data.getMaritalStatus());
        filler.putIfNotNull(PHONE_KEY, data.getPhone());

        instance.db
                .collection("patients")
                .document(instance.uid)
                .set(instance.backupProfile)
                .addOnFailureListener(l -> {
                    instance.backupProfile = null;
                    instance = null;
                    onCreate.onCall(null, l);
                })
                .addOnSuccessListener(l -> {
                    instance.modifiableProfile = MapDeepCopier.deepCopy(instance.backupProfile);
                    onCreate.onCall(true, null);
                });
    }

    static public void logOut(){
        instance = null;
    }

    /**
     * Save the user profile changes to the database
     */
    static public void save(Callback<Boolean, Exception> onSave){
        if(instance != null && instance.modifiableProfile != null){
            //TODO: store the data
            instance.db
                    .collection("patients")
                    .document(instance.uid)
                    .set(instance.modifiableProfile)
                    .addOnCompleteListener(l -> {
                        if(l.isSuccessful()){
                            instance.backupProfile = instance.modifiableProfile;
                            instance.modifiableProfile = MapDeepCopier.deepCopy(instance.backupProfile);
                            onSave.onCall(true, null);
                        }else{
                            onSave.onCall(null, l.getException());
                        }
                    });
        }else{
            onSave.onCall(null, new Exception("Profile is not ready to be saved yet"));
        }
    }

    /**
     *
     * @return true if the profile has been fetched, false otherwise
     */
    static public boolean isFetched(){
        return UserProfile.instance != null && UserProfile.instance.modifiableProfile != null;
    }

    /**
     * Revert changes that have been applied so far with setters methods
     */
    static public void revertChanges(){
        instance.modifiableProfile = MapDeepCopier.deepCopy(instance.backupProfile);
    }

    /**
     * Link this user with a new doctor
     * @param inviteId the invite id used to generate the qr code
     * @param doctorId the doctor id to which to link
     * @param onLink a callback. in case of error, the error message is user friendly
     */
    static public void linkDoctor(String inviteId, String doctorId, Callback<Boolean, Exception> onLink){
        //Get the current list of doctors
        Object temp = instance.modifiableProfile.get(DOCTORS_KEY);

        List<String> doctorIds;
        if(temp == null){
            doctorIds = new ArrayList<String>();
        }else {
            doctorIds = (List<String>)temp;
        }

        //Check if we have room for another doctor
        if(doctorIds.size() >= 10){
            onLink.onCall(null, new Exception("You are already linked to the maximum number of doctors."));
            return;
        }

        //Check if we are already linked to this doctor
        if(doctorIds.contains(doctorId)){
            onLink.onCall(null, new Exception("You are already linked to this doctor."));
            return;
        }

        //Now we re getting ready for the transaction

        //Get a ref to the invite doc
        DocumentReference inviteRef = instance.db
                .collection("invites")
                .document(inviteId);

        //Get a ref to the patient profile
        DocumentReference profileRef = instance.db
                .collection("patients")
                .document(getUid());

        //Get a ref to /doctors/:doctorId/linkedPatients/
        DocumentReference doctorPatientRef = instance.db
                .collection("doctors")
                .document(doctorId)
                .collection("linkedPatients")
                .document(getUid());

        String errorMessage = "This invite is not valid.";

        //Create the object that will be stored at
        // /doctors/:doctorId/linkedPatients/:patientId
        Map<String, Object> profileView = new HashMap<String, Object>();
        profileView.put(GIVEN_NAME_KEY, getGivenName());
        profileView.put(FAMILY_NAME_KEY, getFamilyName());
        profileView.put(DATE_OF_BIRTH_KEY, getDateOfBirth());

        //Start the transaction
        instance.db.runTransaction(t -> {
            //Get the invite
            DocumentSnapshot invite = t.get(inviteRef);

            //Ensure the invite exists
            if(!invite.exists()){
                throw new FirebaseFirestoreException(errorMessage, FirebaseFirestoreException.Code.ABORTED);
            }

            //Ensure the doctor id in the invite matches the doctor id in the qr code
            String inviteDoctorId = invite.getString("doctorId");
            if(!inviteDoctorId.equals(doctorId)){
                throw new FirebaseFirestoreException(errorMessage, FirebaseFirestoreException.Code.ABORTED);
            }

            //Ensure the invite is not expired
            //Note, this code does not throw if validUntil is not specified in the invite
            Timestamp validUntil = invite.getTimestamp("validUntil");
            if(validUntil != null && validUntil.compareTo(Timestamp.now()) < 0 ){
                throw new FirebaseFirestoreException(errorMessage, FirebaseFirestoreException.Code.ABORTED);
            }

            //Finally update patient profile
            t.update(profileRef, DOCTORS_KEY, FieldValue.arrayUnion(doctorId));

            //Update doctor's patients
            t.set(doctorPatientRef, profileView);
            
            //TODO: delete the invite

            return null;

        }).addOnSuccessListener(l -> {
            //The transaction run successfully!,
            // we simply need to update our local copy of the data
            doctorIds.add(doctorId);
            instance.modifiableProfile.put(DOCTORS_KEY, doctorIds);
            instance.backupProfile.put(DOCTORS_KEY, doctorIds);

            onLink.onCall(true, null);

        }).addOnFailureListener(l -> {
            //Something went wrong
            //If we detected an invalid invite we let the user know
            if(l.getMessage() == errorMessage){
                onLink.onCall(null, new Exception(errorMessage));
            }
            //Otherwise we report it as an unknown error
            else{
                onLink.onCall(null, new Exception("Unable to link doctor, try again later."));
            }
        });

    }

    static public void unlink_doctor(String doctorId, Callback<Boolean, Exception> onLink) {
        /**
         * Unlinking a doctor from a patient
         * Get the patient id from the user logged in, then remove the doctor from their doctor list
         * then go to the doctor and remove the patient from their linkedpatients collection.
         * @param doctorId the doctor id to which to link
         * @param onLink a callback. in case of error, the error message is user friendly
         */

        // Grab the doctors and we will check whether or not the doctor has been unlinked already
        Object temp = instance.modifiableProfile.get(DOCTORS_KEY);

        List<String> doctorIds;
        if(temp == null){
            doctorIds = new ArrayList<String>();
        }else {
            doctorIds = (List<String>)temp;
        }

        //Check that the doctor isn't already unlinked
        if(!doctorIds.contains(doctorId)) {
            onLink.onCall(null, new Exception("You have already been unlinked from this doctor."));
            return;
        }

        String errorMessage = "The doctor could not be unlinked.";

        // Get all the references from the db
        // Get a ref to the patient profile
        DocumentReference profileRef = instance.db
                .collection("patients")
                .document(getUid());

        // Get a ref to /doctors/:doctorId/linkedPatients/:patientid
        DocumentReference doctorPatientRef = instance.db
                .collection("doctors")
                .document(doctorId)
                .collection("linkedPatients")
                .document(getUid());

        //Start the transaction
        instance.db.runTransaction(t -> {

            // Update the doctor field with the doctor id removed using arrayRemove.
            t.update(profileRef, DOCTORS_KEY, FieldValue.arrayRemove(doctorId));

            // Remove the patient id (document) from the doctor's linkedpatients.
            t.delete(doctorPatientRef);

            return null;

        }).addOnSuccessListener(l -> {
            // The transaction ran successfully!,
            // We simply need to update our local copy of the data
            doctorIds.remove(doctorId);
            instance.modifiableProfile.put(DOCTORS_KEY, doctorIds);
            instance.backupProfile.put(DOCTORS_KEY, doctorIds);

            onLink.onCall(true, null);

        }).addOnFailureListener(l -> {
            //Something went wrong
            //If something went wrong
            if(l.getMessage() == errorMessage){
                onLink.onCall(null, new Exception(errorMessage));
            }
            //Otherwise we report it as an unknown error
            else{
                onLink.onCall(null, new Exception("Unable to unlink doctor, try again later."));
            }
        });

    }

    static public String getUid() {
        return instance.uid;
    }

    /**
     * Helper method to avoid runtime cast exceptions
     * @param o
     * @return
     */
    static private String stringOrNull(Object o){
        if(o == null){
            return null;
        }
        return (String)o;
    }

    static public String getGivenName() {
        return stringOrNull(instance.modifiableProfile.get(GIVEN_NAME_KEY));
    }

    static public void setGivenName(String givenName){
        instance.modifiableProfile.put(GIVEN_NAME_KEY, givenName);
    }

    static public String getBloodType(){
        return stringOrNull(instance.modifiableProfile.get(BLOOD_TYPE_KEY));
    }

    static public void setBloodType(String bloodType){
        instance.modifiableProfile.put(BLOOD_TYPE_KEY, bloodType);
    }

    static public String getWeight(){
        // return stringOrNull(instance.modifiableProfile.get(WEIGHT_KEY));
        Object o = instance.modifiableProfile.get(WEIGHT_KEY);
        return o==null?null:String.valueOf(o);
    }

    static public void setWeight(String weight){
        instance.modifiableProfile.put(WEIGHT_KEY, weight);
    }

    static public String getHeight(){
        // return stringOrNull(instance.modifiableProfile.get(HEIGHT_KEY));
        Object o = instance.modifiableProfile.get(HEIGHT_KEY);
        return o==null?null:String.valueOf(o);
    }

    static public void setHeight(String height){
        instance.modifiableProfile.put(HEIGHT_KEY, height);
    }

    static public Timestamp getDateOfBirth(){
        Object o = instance.modifiableProfile.get(DATE_OF_BIRTH_KEY);

        return o == null ? null : (Timestamp)o;
    }

    static public void setDateOfBirth(Timestamp timestamp){
        instance.modifiableProfile.put(DATE_OF_BIRTH_KEY, timestamp);
    }

    static public int getAge() {
        Calendar dob = Calendar.getInstance();
        dob.setTime(instance.getDateOfBirth().toDate());
        Calendar today = Calendar.getInstance();


        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);

        return ageInt;
    }


    static public String[] getLinkedDoctorIds(){
        Object o = instance.modifiableProfile.get(DOCTORS_KEY);
        if(o == null){
            return null;
        }
        List<Object> l = (List<Object>) o;
        String[] s = new String[l.size()];

        for(int i = 0; i < l.size(); i++){
            s[i] = (String)l.get(i);
        }
        return s;
    }

    static public String getFamilyName() {
        return stringOrNull(instance.modifiableProfile.get(FAMILY_NAME_KEY));
    }

    static public void setFamilyName(String familyName){
        instance.modifiableProfile.put(FAMILY_NAME_KEY, familyName);
    }

    static public String getEmail(){
        return stringOrNull(instance.modifiableProfile.get(EMAIL_KEY));
    }

    static public String getGender(){
        return stringOrNull(instance.modifiableProfile.get(GENDER_KEY));
    }

    static public  void setGender(String gender){
        instance.modifiableProfile.put(GENDER_KEY, gender);
    }

    static public String getMaritalStatus(){
        return stringOrNull(instance.modifiableProfile.get(MARITAL_STATUS_KEY));
    }

    static public void setMaritalStatus(String maritalStatus){
        instance.modifiableProfile.put(MARITAL_STATUS_KEY, maritalStatus);
    }

    static public String getPhone(){
        return stringOrNull(instance.modifiableProfile.get(PHONE_KEY));
    }

    static public void setPhone(String phone){
        instance.modifiableProfile.put(PHONE_KEY, phone);
    }


    /**
     * Add new notification to the db under the current user
     * @param title the notification title
     * @param description the notification description
     * @param datetime the time and date the notification took place
     */
    static public void uploadNotification(String title, String description, Date datetime) {
        Timestamp time = new Timestamp(datetime);

        Log.i("d", "Uploading");
        HashMap<String, Object> toUpload = new HashMap<>();
        toUpload.put(NOTIFICATION_TITLE, title);
        toUpload.put(NOTIFICATION_DESCRIPTION, description);
        toUpload.put(NOTIFICATION_TIME, time);

        instance.db
                .collection("patients")
                .document(instance.uid)
                .collection("notificationHistory")
                .add(toUpload)
                .addOnCompleteListener(l -> {
                    if (l.isSuccessful()) {
                        Log.i("d", "upload successful");
                    } else {
                        Log.i("d", "upload unsuccessful");
                    }
                });
    }

}