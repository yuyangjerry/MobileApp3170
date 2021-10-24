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

    private ArrayList<Doctor> doctorList;
    private RecyclerView recyclerView;
    private DoctorAdpter.RecyclerViewClickListener listener;
    private DoctorAdpter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Button linkDoctorButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctors, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.doctorList);
        doctorList = new ArrayList<>();

        setAdapter();
        getDoctor();

        //user can pull down the doctor list to update the list
        //ref: https://www.youtube.com/watch?v=Ffa0Mtd21_M
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //clear the doctor list and get all the doctors profile again
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

    /*
    this function will get all the doctors linked and get the profile
     */
    private void getDoctor(){
        //clear the list
        adapter.clear();
        // 1. get all doctor ids
        String doctorIds[] = UserProfile.getLinkedDoctorIds();
        if(doctorIds == null || doctorIds.length == 0){
            //Well, seems like there are no linked doctors
            new AlertDialog.Builder(getContext())
                    .setTitle("0 doctors linked")
                    .setMessage("There are no doctors linked")
                    .setPositiveButton("Ok", null)
                    .show();
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
                        //Success!, we can use doctors[index] now
                        DoctorProfile doc = doctors[index];
                        Log.i("DOCTORS", "Got doctor profile!" + doc.getUid());
                        doctorList.add(new Doctor(doc.getUid(), doc.getGivenName(), doc.getFamilyName(), doc.getEmail(), doc.getPhoneNumber(), doc.getPlaceOfPractice()));
                        //update the doctor array and UI
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    /*
    initialise adapter
     */
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
            // Create an intent to go to the doctor profile page.
            Context context = getContext();
            Intent intent = new Intent(context, DoctorProfileActivity.class);
            //send extra information, doctor profile
            intent.putExtra("doctorid", doctorList.get(position).getDoctorID());
            intent.putExtra("doctorgivenname", doctorList.get(position).getDoctorGivenName());
            intent.putExtra("doctorfamilyname", doctorList.get(position).getDoctorFamilyName());
            intent.putExtra("email", doctorList.get(position).getDoctorEmail());
            intent.putExtra("phonenumber", doctorList.get(position).getPhoneNumber());
            intent.putExtra("placeofpractice", doctorList.get(position).getPlaceOfPractice());
            startActivity(intent);
        };
    }

    /**
     * Perform the actual linking after scanning the qr code
     * @param qrCodeData
     */
    private void linkDoctor(String qrCodeData){

        QrCodeValidator validator = new QrCodeValidator(qrCodeData);

        if(validator.isValid()){
            //verify successful
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
                                        //link unsuccessful, notify user
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Unable to link doctor")
                                                .setMessage(error.getMessage())
                                                .setPositiveButton("Ok", null)
                                                .show();
                                    }else{
                                        //link successful, notify user
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Linked successfully!")
                                                .setPositiveButton("Ok", null)
                                                .show();
                                        //get the doctor profile and update the UI
                                        getDoctor();
                                    }
                                }
                        );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        }else{
            //verify unsuccessful
            new AlertDialog.Builder(getContext())
                    .setTitle("Invalid QR-Code")
                    .setMessage("The scanned QR-Code is invalid")
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }
}
