package com.FIT3170.HealthMonitor.database;

import com.FIT3170.HealthMonitor.FireBaseAuthClient;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        if(!isFetched()){
            UserProfile.instance = new UserProfile();
            UserProfile.instance.downloadProfile(onFetch);
        }else{
            onFetch.onCall(null, new Exception("Profile already fetched"));
        }
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

    static public String getUid() {
        return instance.uid;
    }

    static public String getGivenName() {
        return (String)instance.modifiableProfile.get(GIVEN_NAME_KEY);
    }

    static public void setGivenName(String givenName){
        instance.modifiableProfile.put(GIVEN_NAME_KEY, givenName);
    }

    static public String getBloodType(){
        return (String)instance.modifiableProfile.get(BLOOD_TYPE_KEY);
    }

    static public void setBloodType(String bloodType){
        instance.modifiableProfile.put(BLOOD_TYPE_KEY, bloodType);
    }

    static public String getWeight(){
        return (String)instance.modifiableProfile.get(WEIGHT_KEY);
    }

    static public void setWeight(String weight){
        instance.modifiableProfile.put(WEIGHT_KEY, weight);
    }

    static public String getHeight(){
        return (String)instance.modifiableProfile.get(HEIGHT_KEY);
    }

    static public void setHeight(String height){
        instance.modifiableProfile.put(HEIGHT_KEY, height);
    }

    static public Timestamp getDateOfBirth(){
        return (Timestamp) instance.modifiableProfile.get(DATE_OF_BIRTH_KEY);
    }

    static public void setDateOfBirth(Timestamp timestamp){
        instance.modifiableProfile.put(DATE_OF_BIRTH_KEY, timestamp);
    }

    static public String[] getLinkedDoctorIds(){
        List<String> doctorIds = (List<String>) instance.modifiableProfile.get(DOCTORS_KEY);
        return (String[])doctorIds.toArray();
    }

    static public String getFamilyName() {
        return (String)instance.modifiableProfile.get(FAMILY_NAME_KEY);
    }

    static public void setFamilyName(String familyName){
        instance.modifiableProfile.put(FAMILY_NAME_KEY, familyName);
    }

    static public String getEmail(){
        return (String)instance.modifiableProfile.get(EMAIL_KEY);
    }

    static public String getGender(){
        return (String)instance.modifiableProfile.get(GENDER_KEY);
    }

    static public  void setGender(String gender){
        instance.modifiableProfile.put(GENDER_KEY, gender);
    }

    static public String getMaritalStatus(){
        return (String)instance.modifiableProfile.get(MARITAL_STATUS_KEY);
    }

    static public void setMaritalStatus(String maritalStatus){
        instance.modifiableProfile.put(MARITAL_STATUS_KEY, maritalStatus);
    }

    static public String getPhone(){
        return (String)instance.modifiableProfile.get(PHONE_KEY);
    }

    static public void setPhone(String phone){
        instance.modifiableProfile.put(PHONE_KEY, phone);
    }

}