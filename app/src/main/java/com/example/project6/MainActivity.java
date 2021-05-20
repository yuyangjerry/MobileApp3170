package com.example.project6;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This is the main activity, i.e. the activity that appears when the app is launched.
 * The user is asked to sign in with his email and password, after logging in, he is redirected
 * to the UserProfile activity, where he can see his profile info.
 */
public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "com.mainApp";

    EditText emailInput;    //EditText objects represent editable input fields
    EditText passwordInput;

    Button signInButton;    //Declare the signIn Button property

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        NotificationBuilder builder = new NotificationBuilder();
        builder.createNotification(this, "my notification", "this is a notification");
        //find the input fields and the sign in button views by id and
        //assign them to the respective references.
        //The R class is a reference to the project resources, such as the view's ids, images, layout
        //files etc..

        emailInput = findViewById(R.id.email_input);


        signInButton = findViewById(R.id.sign_in_button);

        Context context = this; // the context is a reference to the activity itself
                                // that we are going to need in the listener below

        //Set an onCLickListener on the button. This is an anonymous subclass of
        //View.OnClickListener whose onClick method will be called when the user clicks the button.
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Test";//getString(R.string.channel_name);
            String description = "Description"; //getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}