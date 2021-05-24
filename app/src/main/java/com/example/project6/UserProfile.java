package com.example.project6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfile extends AppCompatActivity {

    TextView firstName;   //we are going to fill this textviews with the user information
    TextView email;
    TextView lastName;
    TextView uuid;

    Button linkDoctor;

    public final static int QR_CODE_REQUEST_CODE = 1;
    public final static int CAMERA_PERMISSION_REQUEST_CODE = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //get all needed views by id
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        uuid = findViewById(R.id.uuid);

        linkDoctor = findViewById(R.id.link_doctor_button);

        //get the auth client
//        AuthClient auth = FakeAuthClient.getClient();
//
//        //get the currently signed in user
//        User user = auth.getSignedInUser();
//
//        //set the text of the views with user data
//        firstName.setText(user.getFirstName());
//        lastName.setText(user.getLastName());
//        email.setText(user.getEmail());
//        uuid.setText(user.getUuid().toString());


        //When patient wants to link to doctor
        linkDoctor.setOnClickListener(l -> {
            // if patient has not given camera permission yet
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //ask for camera permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }else{
                //If we already have permission, just lounch the QRscanner activity which will take care of scanning
                Intent intent = new Intent(this, QRScanner.class);
                startActivityForResult(intent, QR_CODE_REQUEST_CODE);
            }
        });
    }

    // this will be triggered when the user responds to camera permission dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            //if the permission was granted, just launch the QRscanner
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, QRScanner.class);
                startActivityForResult(intent, QR_CODE_REQUEST_CODE);
            } else {  //Otherwise show an error message
                Toast.makeText(this, "You need to provide permission to use your camera in order to scan QRcode.", Toast.LENGTH_LONG).show();
            }
        }
    }

    //This will be triggered once the QRscanner has scanned something
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent response) {
        if(requestCode == QR_CODE_REQUEST_CODE) {
            //if the user scanned a qr code
            if(resultCode == RESULT_OK){
                //show the url
                String url = response.getStringExtra(QRScanner.RESPONSE_INTENT_URL_KEY);
                Toast.makeText(this, "URL: " + url, Toast.LENGTH_LONG).show();
            }else{
                //otherwise show an error message
                Toast.makeText(this, "Could not scan QRcode.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, response);
        }
    }
}