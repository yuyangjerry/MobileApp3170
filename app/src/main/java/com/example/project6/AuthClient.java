package com.example.project6;

import com.google.firebase.auth.FirebaseUser;

/**
 * This class represents an Authorisation client that allows users to signIn and signOut
 */
abstract class AuthClient {
    abstract boolean signIn(String email, String password, SignInConsumer consumer);

    abstract void signOut();

    abstract FirebaseUser getSignedInUser();
}
