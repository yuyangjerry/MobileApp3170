package com.example.project6;


import com.google.firebase.auth.FirebaseUser;

public interface SignInConsumer {
    public void onSigninSuccess(FirebaseUser user);

    public void onSigninFailure();
}