package com.FIT3170.HealthMonitor;

import com.google.firebase.auth.FirebaseUser;

public interface SignUpConsumer {
    public void onSignupSuccess(FirebaseUser user);

    public void onSignupFailure();
}
