package com.FIT3170.HealthMonitor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;

public class DoctorsFragment extends Fragment {

    public final static int QR_CODE_REQUEST_CODE = 1;
    public final static int CAMERA_PERMISSION_REQUEST_CODE = 2;

    private Button linkDoctorButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctors, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO:
        // 1. get all doctor ids
        // 1. get all doctors profiles
        // 1. for every doctor
        //      - add a fragment that links to the doctor profile

        linkDoctorButton = view.findViewById(R.id.link_new_doctor_button);

        linkDoctorButton.setOnClickListener(l -> {
            Context context = getContext();
            // if patient has not given camera permission yet
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //ask for camera permission
                Activity activity = getActivity();
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }else{
                //If we already have permission, just launch the QRscanner activity which will take care of scanning
                Intent intent = new Intent(context, QRScanner.class);
                startActivityForResult(intent, QR_CODE_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == QR_CODE_REQUEST_CODE) {
            Context context = getContext();
            //if the user scanned a qr code
            if(resultCode == Activity.RESULT_OK){
                //show the url
                String url = data.getStringExtra(QRScanner.RESPONSE_INTENT_URL_KEY);
                Toast.makeText(context, "URL: " + url, Toast.LENGTH_LONG).show();
            }else{
                //otherwise show an error message
                Toast.makeText(context, "Could not scan QRcode.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            //if the permission was granted, just launch the QRscanner
            Context context = getContext();
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context, QRScanner.class);
                startActivityForResult(intent, QR_CODE_REQUEST_CODE);
            } else {  //Otherwise show an error message
                Toast.makeText(context, "You need to provide permission to use your camera in order to scan QRcode.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
