package com.FIT3170.HealthMonitor;

import androidx.annotation.NonNull;

import com.FIT3170.HealthMonitor.database.UserProfile;
import com.FIT3170.HealthMonitor.database.UserSignUpData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class FireBaseAuthClient {

    private static FireBaseAuthClient instance = null;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser = null;


    static boolean signIn(String email, String password, SignInConsumer consumer) {
        if (instance == null){
            instance = new FireBaseAuthClient();
        }

        instance.mAuth = FirebaseAuth.getInstance();
        instance.mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    instance.currentUser = instance.mAuth.getCurrentUser();
                    UserProfile.fetch((v, err) -> {
                        if(err != null){
                            consumer.onSigninFailure();
                        }else{
                            consumer.onSigninSuccess(instance.currentUser);
                        }
                    });
                }else{
                    consumer.onSigninFailure();
                }
            }
        });
          
        return false;
    }

    /**
     * Sign up crates a user inside Authentication and a patient inside Firestore
     * @param data the data to populate the patient in Firestore
     * @param password the user password in cleartext
     * @param consumer a SignUpConsumer that will be called depending on the outcome.
     */
    static void SignUp(UserSignUpData data, String password, SignUpConsumer consumer){
        if (instance == null){
            instance = new FireBaseAuthClient();
        }

        instance.mAuth = FirebaseAuth.getInstance();

        instance.mAuth
                .createUserWithEmailAndPassword(data.getEmail(), password)
                .addOnSuccessListener(l -> {
                    instance.currentUser = instance.mAuth.getCurrentUser();
                    //Create the user profile in firestore
                    UserProfile.create(data, (success, error) -> {
                        if(error != null){
                            consumer.onSignupFailure();
                        }else{
                            consumer.onSignupSuccess(instance.currentUser);
                        }
                    });
                })
                .addOnFailureListener(l -> {
                    consumer.onSignupFailure();
                });
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        UserProfile.logOut();
    }

    public static FirebaseUser getSignedInUser() {
        return instance.currentUser;
    }
}
