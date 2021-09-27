package com.FIT3170.HealthMonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.FIT3170.HealthMonitor.adapters.ProfileGVAdapter;
import com.FIT3170.HealthMonitor.database.DoctorProfile;
import com.FIT3170.HealthMonitor.database.UserProfile;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DoctorProfileActivity extends AppCompatActivity {

    GridView doctorProfileGV;
    TextView doctorProfileTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the intent from DoctorsFragment, and get the string of the doctorid.
//        Intent intent = getIntent();
//        String doctorId = intent.getStringExtra("doctorid");

        // get the details from the selected doctor
        String doctorId = "Not found!";
        String doctorGivenName = "Not found!";
        String email = "Not found!";
        String doctorFamilyName = "Not found!";
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            doctorId = extras.getString("doctorid");
            doctorGivenName = extras.getString("doctorgivenname");
            email = extras.getString("email");
            doctorFamilyName = extras.getString("doctorfamilyname");
        }
        // -----------------------

        // Currently the retrieved doctorProfile returns null when the attributes are called upon.
        DoctorProfile doctorProfile = new DoctorProfile(doctorId, (success, error) -> {
            if (error != null) {
                Log.i("DOCTORS", "Couldn't get doctor profile :(");
                //oops, something went wrong, probably a network error
            } else {
                Log.i("DoctorProfile", "Doctor Profile Retrieved");
            }
        });

        setContentView(R.layout.activity_doctor_profile);
        super.onCreate(savedInstanceState);

        // Get the required views from the activity_doctor_profile.xml
        doctorProfileGV = findViewById(R.id.doctorProfileGV);
        doctorProfileTV = findViewById(R.id.doctorName);
        //Date dob = doctorProfile.getDateOfBirth().toDate();

        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        //String dobString = formatter.format(dob);


        // Create an arraylist of ProfileAttributeModel so we can populate the gridview using an adapter
        // Need to sort out the firebase so that the doctor information is consistent.

        String doctorName = doctorGivenName + " " + doctorFamilyName;

        ArrayList<ProfileAttributeModel> profileModelArrayList = new ArrayList<ProfileAttributeModel>();
        //profileModelArrayList.add(new ProfileAttributeModel("Date of Birth", dobString));
        //profileModelArrayList.add(new ProfileAttributeModel("Full Name", doctorName));
        profileModelArrayList.add(new ProfileAttributeModel("Email", email));
        //profileModelArrayList.add(new ProfileAttributeModel("Place of Practice", doctorProfile.getPlaceOfPractice()));
        //profileModelArrayList.add(new ProfileAttributeModel("Phone", doctorProfile.getPhoneNumber()));

        ProfileGVAdapter adapter = new ProfileGVAdapter(this, profileModelArrayList);
        doctorProfileGV.setAdapter(adapter);

        doctorProfileTV.setText(doctorName);

    }
}