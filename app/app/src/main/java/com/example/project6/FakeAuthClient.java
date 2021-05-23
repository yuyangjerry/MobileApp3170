package com.example.project6;

import java.util.UUID;

/**
 * This class is a dummy implementation of the AuthClient
 * used for demonstration purposes.
 *
 * In particular, this class is also a singleton, so that we can store the
 * currently logged in user in it and access it from different activities.
 */
public class FakeAuthClient extends AuthClient{
    private User signedInUser;
    static private FakeAuthClient client;

    private FakeAuthClient(){}

    //do the singleton instantiation
    static public FakeAuthClient getClient(){
        if(client == null)
            client = new FakeAuthClient();

        return client;
    }


    @Override
    boolean signIn(String email, String password){
        //all credentials will result in the user John Smith
        this.signedInUser = new User("John", "Smith", email, UUID.randomUUID());
        return true;
    }

    @Override
    User getSignedInUser(){
        return this.signedInUser;
    }

    @Override
    void signOut(){
        this.signedInUser = null;
    }
}
