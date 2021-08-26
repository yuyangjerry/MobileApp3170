package com.FIT3170.HealthMonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.FIT3170.HealthMonitor.database.ReadingUploader;
import com.google.firebase.auth.FirebaseUser;

public class UserProfile extends AppCompatActivity {

    //we are going to fill this textviews with the user information
    TextView email;
    TextView uuid;

    Button notificationButton;

    public final static int QR_CODE_REQUEST_CODE = 1;
    public final static int CAMERA_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //get all needed views by id
        email = findViewById(R.id.email);
        uuid = findViewById(R.id.uuid);

        notificationButton = findViewById(R.id.notificationButton);

        //get the currently signed in user
        FirebaseUser user = FireBaseAuthClient.getSignedInUser();

        //set the text of the views with user data
        email.setText(user.getEmail());
        uuid.setText(user.getUid());

        createNotificationChannel();
        NotificationBuilder builder = new NotificationBuilder();
        notificationButton.setOnClickListener(l -> {
            builder.createNotification(this, "Abnormal Heart Rate", "An abnormal heart rate was detected. We recommend you get proper medical assistance.");

            // FOR TESTING add Data
            // TODO: DELETE THIS
            // this adds a value to reading uploader
            // should be called when a reading is received from the device
            ReadingUploader.getInstance().addData(2000);

        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Test";//getString(R.string.channel_name);
            String description = "Description"; //getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("com.FIT3170.HealthMonitor", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}