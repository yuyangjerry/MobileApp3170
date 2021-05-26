package com.FIT3170.HealthMonitor;


import com.google.firebase.auth.FirebaseUser;

public interface SignInConsumer {
    public void onSigninSuccess(FirebaseUser user);

    public void onSigninFailure();
}