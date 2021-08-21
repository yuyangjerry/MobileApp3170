package com.FIT3170.HealthMonitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;

public class DoctorsFragment extends Fragment {

    Button linkDoctorButton;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO:
        // 1. get all doctor ids
        // 1. get all doctors profiles
        // 1. for every doctor
        //      - add a fragment that links to the doctor profile

        linkDoctorButton = view.findViewById(R.id.link_doctor_button);

//        linkDoctorButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO: open the qr code scanner
//            }
//        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctors, container, false);
    }
}
