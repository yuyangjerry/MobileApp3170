package com.FIT3170.HealthMonitor;

import androidx.annotation.NonNull;

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
                    consumer.onSigninSuccess(instance.currentUser);

                }else{
                    consumer.onSigninFailure();
                }
            }
        });
          
        return false;
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public static FirebaseUser getSignedInUser() {
        return instance.currentUser;
    }
}
