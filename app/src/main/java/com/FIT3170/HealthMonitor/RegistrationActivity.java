package com.FIT3170.HealthMonitor;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.Timestamp;

import com.FIT3170.HealthMonitor.database.UserSignUpData;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.FIT3170.HealthMonitor.databinding.ActivityRegistrationBinding;
import com.google.firebase.firestore.auth.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class RegistrationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // input fields
    private EditText emailInput;
    private EditText pswrdInput;
    private EditText pswrdConfirmInput;
    private EditText givenNameInput;
    private EditText surnnameInput;
    private EditText dobInput;


    private Spinner genderSpinner;
    private Spinner stateSpinner;

    private EditText addressInput;
    private EditText suburbInput;
    private EditText postcodeInput;

    private Button regBtn;
    private Button backBtn;
    private Timestamp dob;


    private AppBarConfiguration appBarConfiguration;
    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.fragment_registration);
        super.onCreate(savedInstanceState);
        initSpinner();
        getComponentIDs();

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());


        setSupportActionBar(binding.toolbar);

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_registration);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * Get xml components by their ids and initialise attributes
     */
    private void getComponentIDs(){
        emailInput = findViewById(R.id.regEmail);
        pswrdInput = findViewById(R.id.regPassword);
        pswrdConfirmInput = findViewById(R.id.regPasswordConfirm);
        givenNameInput = findViewById(R.id.regGivenName);
        surnnameInput = findViewById(R.id.regSurname);

        addressInput = findViewById(R.id.regPostalAddress);
        suburbInput = findViewById(R.id.regSuburb);
        postcodeInput = findViewById(R.id.regPostCode);

        regBtn = findViewById(R.id.regButton);
        regBtn.setOnClickListener(view -> {
            register();
        });

        backBtn = findViewById(R.id.regBackButton);
        backBtn.setOnClickListener(view -> {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        });

        dobInput = findViewById(R.id.regDob);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this, 2021, 8, 30);
        dobInput.setOnClickListener(view -> {
                    datePickerDialog.show();
        });

    }

    /**
     * Called on registration button press
     */
    private void register(){
        String email = emailInput.getText().toString();
        String givenName = givenNameInput.getText().toString();
        String surnname = surnnameInput.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        String address = addressInput.getText().toString();
        String suburb = suburbInput.getText().toString();
        String postcode = postcodeInput.getText().toString();
        String state = stateSpinner.getSelectedItem().toString();

        String pswrd = pswrdInput.getText().toString();
        String pswrdConfirm = pswrdConfirmInput.getText().toString();

        UserSignUpData newUser = new UserSignUpData();
        newUser.setGivenName(givenName);
        newUser.setFamilyName(surnname);
        newUser.setDateOfBirth(dob);
        newUser.setEmail(email);
        newUser.setGender(gender);

    }

    /**
     * Populate the spinner with values
     */
    private void initSpinner(){
        // Set up gender spinner
        genderSpinner = (Spinner) findViewById(R.id.regGender);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> genAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        genderSpinner.setAdapter(genAdapter);

        // Set up state spinner
        stateSpinner = (Spinner) findViewById(R.id.regState);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.aus_states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateSpinner.setAdapter(stateAdapter);
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dobInput.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
        // TODO: Store values in timestamp object
        Date date = new Date(year, month, day);
        dob = new Timestamp(date);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_registration);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        showDate(year, month + 1, dayOfMonth);
    }
}