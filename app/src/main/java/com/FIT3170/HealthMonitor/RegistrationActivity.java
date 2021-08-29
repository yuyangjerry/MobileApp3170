package com.FIT3170.HealthMonitor;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.FIT3170.HealthMonitor.databinding.ActivityRegistrationBinding;

public class RegistrationActivity extends AppCompatActivity {

    // input fields
    private EditText emailInput;
    private EditText pswrdInput;
    private EditText pswrdConfirmInput;
    private EditText givenNameInput;
    private EditText surnnameInput;
    private EditText addressInput;
    private Spinner genderSpinner;

    private Button regBtn;


    private AppBarConfiguration appBarConfiguration;
    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSpinner();
        getComponentIDs();

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_registration);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

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

        regBtn = findViewById(R.id.regButton);
        regBtn.setOnClickListener(view -> {
            register();
        });
    }

    /**
     * Called on registration button press
     */
    private void register(){
        String email = emailInput.getText().toString();
        String givenName = givenNameInput.getText().toString();
        String surnname = surnnameInput.getText().toString();
        String pswrd = pswrdInput.getText().toString();
        String pswrdConfirm = pswrdConfirmInput.getText().toString();
    }

    /**
     * Populate the spinner with values
     */
    private void initSpinner(){
        // Set up gender spinner
        genderSpinner = (Spinner) findViewById(R.id.regGender);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        genderSpinner.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_registration);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}