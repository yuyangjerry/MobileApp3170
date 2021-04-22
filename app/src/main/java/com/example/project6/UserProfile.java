package com.example.project6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    TextView firstName;   //we are going to fill this textviews with the user information
    TextView email;
    TextView lastName;
    TextView uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //get all needed views by id
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        uuid = findViewById(R.id.uuid);

        //get the auth client
        AuthClient auth = FakeAuthClient.getClient();

        //get the currently signed in user
        User user = auth.getSignedInUser();

        //set the text of the views with user data
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        uuid.setText(user.getUuid().toString());
    }
}