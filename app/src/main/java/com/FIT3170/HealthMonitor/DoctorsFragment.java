package com.FIT3170.HealthMonitor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.FIT3170.HealthMonitor.adapters.DoctorAdpter;
import com.FIT3170.HealthMonitor.database.DoctorProfile;
import com.FIT3170.HealthMonitor.database.UserProfile;

import java.util.ArrayList;

public class DoctorsFragment extends Fragment {

    public final static int QR_CODE_REQUEST_CODE = 1;
    public final static int CAMERA_PERMISSION_REQUEST_CODE = 2;

    //========
    private ArrayList<Doctor> doctorList;
    private RecyclerView recyclerView;
    private DoctorAdpter.RecyclerViewClickListener listener;
    private DoctorAdpter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    //========

    private Button linkDoctorButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctors, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //=======
        recyclerView = view.findViewById(R.id.doctorList);
        doctorList = new ArrayList<>();

        //setDoctorInfo();
        setAdapter();
        getDoctor();
        //=======

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.clear();
            getDoctor();
            swipeRefreshLayout.setRefreshing(false);
        });

        linkDoctorButton = view.findViewById(R.id.link_new_doctor_button);

        linkDoctorButton.setOnClickListener(l -> {
            Context context = getContext();
            // if patient has not given camera permission yet
            assert context != null;
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //ask for camera permission
                Activity activity = getActivity();
                assert activity != null;
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                //If we already have permission, just launch the QRscanner activity which will take care of scanning
                Intent intent = new Intent(context, QRScanner.class);
                //startActivityForResult(intent, QR_CODE_REQUEST_CODE);
                launchQRActivity.launch(intent);
            }
        });
    }

    // Create lanucher variable inside onAttach or onCreate or global
    ActivityResultLauncher<Intent> launchQRActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Context context = getContext();
                //if the user scanned a qr code
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //show the url
                    String qrCodeData = result.getData().getStringExtra(QRScanner.RESPONSE_INTENT_QR_DATA_KEY);
                    linkDoctor(qrCodeData);
                } else {
                    //otherwise show an error message
                    Toast.makeText(context, "Could not scan QRcode.", Toast.LENGTH_LONG).show();
                }
            });

//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode == QR_CODE_REQUEST_CODE) {
//            Context context = getContext();
//            //if the user scanned a qr code
//            if(resultCode == Activity.RESULT_OK){
//                //show the url
//                String qrCodeData = data.getStringExtra(QRScanner.RESPONSE_INTENT_QR_DATA_KEY);
//                linkDoctor(qrCodeData);
//            }else{
//                //otherwise show an error message
//                Toast.makeText(context, "Could not scan QRcode.", Toast.LENGTH_LONG).show();
//            }
//        }
//        else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            //if the permission was granted, just launch the QRscanner
            Context context = getContext();
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context, QRScanner.class);
                launchQRActivity.launch(intent);
            } else {  //Otherwise show an error message
                Toast.makeText(context, "You need to provide permission to use your camera in order to scan QRcode.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getDoctor(){
        // 1. get all doctor ids
        adapter.clear();
        String doctorIds[] = UserProfile.getLinkedDoctorIds();
        if(doctorIds == null || doctorIds.length == 0){
            //Well, seems like there are no linkded doctors
            //TODO: display that there are no linked doctors
        }else{
            DoctorProfile doctors[] = new DoctorProfile[doctorIds.length];

            // 2. get all doctors profiles
            for (int i = 0; i < doctors.length; i++) {
                Log.i("DOCTORS", doctorIds[i]);
                //Re-declaring i as as final so that we can safely access it inside the callback
                final int index = i;
                doctors[index] = new DoctorProfile(doctorIds[index], (succes, error) -> {

                    if (error != null) {
                        Log.i("DOCTORS", "Couldn't get doctor profile :(");
                        //oops, something went wrong, probably a network error
                    } else if (doctors[index] != null) {
                        //Success!, we can use doctors[imdex] now
                        DoctorProfile doc = doctors[index];
                        Log.i("DOCTORS", "Got doctor profile!" + doc.getUid());
                        //TODO: put doc into a vew in the doctor list
                        doctorList.add(new Doctor(doc.getUid(), doc.getGivenName(), doc.getFamilyName(), doc.getEmail(), doc.getPhoneNumber(), doc.getPlaceOfPractice()));
                        adapter.notifyDataSetChanged();
                        //TODO: when the user clicks on "view doctor profile button"
                        // for this user, put doctorIds[index] into an intent and send it to the doctor
                        // profile fragment/activity
                    }
                });
            }
        }
    }

    private void setAdapter(){
        setOnClickListener();
        adapter = new DoctorAdpter(doctorList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        listener = (v, position) -> {
            // Create an intent to go to the doctor profile.
            Context context = getContext();
            Intent intent = new Intent(context, DoctorProfileActivity.class);
            intent.putExtra("doctorid", doctorList.get(position).getDoctorID());
            intent.putExtra("doctorgivenname", doctorList.get(position).getDoctorGivenName());
            intent.putExtra("doctorfamilyname", doctorList.get(position).getDoctorFamilyName());
            intent.putExtra("email", doctorList.get(position).getDoctorEmail());
            intent.putExtra("phonenumber", doctorList.get(position).getPhoneNumber());
            intent.putExtra("placeofpractice", doctorList.get(position).getPlaceOfPractice());
            startActivity(intent);
        };
    }

//    private void setDoctorInfo(){
//        doctorList.add(new Doctor("papa", "sa"));
//        doctorList.add(new Doctor("dada","as"));
//        doctorList.add(new Doctor("dada","as"));
//        doctorList.add(new Doctor("dada","as"));
//        doctorList.add(new Doctor("dada","as"));
//    }

    /**
     * Perform the actual linking after scanning the qr code
     * @param qrCodeData
     */
    private void linkDoctor(String qrCodeData){

        QrCodeValidator validator = new QrCodeValidator(qrCodeData);

        if(validator.isValid()){
            new AlertDialog.Builder(getContext())
                    .setTitle("Are you sure?")
                    .setMessage("This doctor will have access to your sensor data after linking")
                    .setPositiveButton("Link", (dialog, which) -> {
                        UserProfile.linkDoctor(
                                validator.getInviteId(),
                                validator.getDoctorId(),

                                (v, error) -> {
                                    dialog.dismiss();

                                    if(error != null){
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Unable to link doctor")
                                                .setMessage(error.getMessage())
                                                .setPositiveButton("Ok", null)
                                                .show();
                                    }else{
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Linked successfully!")
                                                .setPositiveButton("Ok", null)
                                                .show();

                                        //TODO: Updated the UI after linking a new doctor

//                                        getFragmentManager().beginTransaction()
//                                                .detach(this)
//                                                .attach(this)
//                                                .commit();

                                        //doctorList.add(new Doctor(validator.getDoctorId(), validator.getInviteId(), validator.getInviteId(), validator.getInviteId(), validator.getInviteId(), validator.getInviteId()));
                                        //adapter.notifyDataSetChanged();
                                        getDoctor();
                                    }
                                }
                        );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        }else{
            new AlertDialog.Builder(getContext())
                    .setTitle("Invalid QR-Code")
                    .setMessage("The scanned QR-Code is invalid")
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }
}
