package com.FIT3170.HealthMonitor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.FIT3170.HealthMonitor.adapters.ProfileGVAdapter;
import com.FIT3170.HealthMonitor.database.UserProfile;
import com.google.firebase.firestore.auth.User;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Fragment for the user profile
 * Creates a grid view and displays the users information
 */
public class UserProfileFragment extends Fragment {

    GridView profileGV;
    TextView profileTV;

    public UserProfileFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    /**
     * Can add more information to the profile model array list and it will be displayed
     * This can be any information from the user profile
     * Used the UserProfile singleton class to obtain the data to be displayed
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileGV = view.findViewById(R.id.idGVcourses);
        profileTV = view.findViewById(R.id.patientName);

        String patientName = UserProfile.getGivenName() + " " + UserProfile.getFamilyName();

        profileTV.setText(patientName);


        Date dob = UserProfile.getDateOfBirth().toDate();
        String height = UserProfile.getHeight() + "cm";
        String weight = UserProfile.getWeight() + "kg";
        //Format the dob
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");

        String dobString = formatter.format(dob);

        //Add the fields to the array
        //Each field must be in a model
        ArrayList<ProfileAttributeModel> profileModelArrayList = new ArrayList<ProfileAttributeModel>();
        profileModelArrayList.add(new ProfileAttributeModel("Date of Birth", dobString));
        profileModelArrayList.add(new ProfileAttributeModel("Blood", UserProfile.getBloodType()));
        profileModelArrayList.add(new ProfileAttributeModel("Gender", UserProfile.getGender()));
        profileModelArrayList.add(new ProfileAttributeModel("Height", height));
        profileModelArrayList.add(new ProfileAttributeModel("Weight", weight));
        profileModelArrayList.add(new ProfileAttributeModel("Marital Status", UserProfile.getMaritalStatus()));

        //Create the adapter with data nad set the adapter
        ProfileGVAdapter adapter = new ProfileGVAdapter(getActivity(), profileModelArrayList);
        profileGV.setAdapter(adapter);
    }
}