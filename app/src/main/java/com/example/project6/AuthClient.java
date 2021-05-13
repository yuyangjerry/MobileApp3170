package com.example.project6;

/**
 * This class represents an Authorisation client that allows users to signIn and signOut
 */
abstract class AuthClient {
    abstract boolean signIn(String email, String password);

    abstract void signOut();

    abstract User getSignedInUser();
}
