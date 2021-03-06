package com.FIT3170.HealthMonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.FIT3170.HealthMonitor.DashBoard.DashBoardFragment;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothService;
import com.FIT3170.HealthMonitor.database.UserProfile;
import com.FIT3170.HealthMonitor.services.NotificationService;
import com.FIT3170.HealthMonitor.services.UploadingService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;

/**
 * This is the main activity, i.e. the activity that appears when the app is launched.
 * The user is asked to sign in with his email and password, after logging in, he is redirected
 * to the UserProfile activity, where he can see his profile info.
 */
public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "com.mainApp";
    private NavigationView navigationView;
    private Button signInButton;    //Declare the signIn Button property
    private Toolbar toolbar ;
    private FirebaseAuth mAuth;
    private TextView drawer_header_user;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_drawer);
        Context context = this; // the context is a reference to the activity itself
        InitialiseFields();
        // Start the services of the app
        startServices();
        // load the home fragment
        loadFragment(new HomeFragment());
    }

    /**
     * Starts all the services required for the app
     * Bluetooth service must be created before the notification service as it is a dependent
     * TODO: Look to see if bluetooth service should instantiate all its dependent services
     */
    private void startServices(){
        // Start the bluetooth service
        Intent bluetoothServiceIntent = new Intent(this, BluetoothService.class);
        this.startService(bluetoothServiceIntent);

        // Start the bluetooth service
        Intent notificationServiceIntent = new Intent(this, NotificationService.class);
        this.startService(notificationServiceIntent);

        // Start the uploading service
        Intent uploadingServiceIntent = new Intent(this, UploadingService.class);
        this.startService(uploadingServiceIntent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    private void InitialiseFields() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationViewDrawer);
        setupDrawerContent( navigationView);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void SendUserToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        drawer_header_user = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user);
        if(drawer_header_user != null){
            String familyName = UserProfile.getFamilyName();
            String givenName = UserProfile.getGivenName();

            if(givenName == null)
                givenName = "";
            if(familyName == null)
                familyName = "";
            drawer_header_user.setText(givenName + " " + familyName);
        }
    }

    private int currentFragmentID;
    public void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        //Add more items via res/menu/drawermenu.xml
        int id = item.getItemId();
        if (currentFragmentID == id) {
            item.setChecked(true);
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (id == R.id.Home) {
            loadFragment(new HomeFragment());
        }
        if (id == R.id.dashboard) {
            loadFragment(new DashBoardFragment(this));
        }
        if (id == R.id.doctors) {
            loadFragment(new DoctorsFragment());
        }
        if (id == R.id.user_profile) {
            loadFragment(new UserProfileFragment());
        }
        if (id == R.id.notifications) {
            loadFragment(new NotificationHistory());
        }
        if (id == R.id.signOut) {
            mAuth.signOut();
            FireBaseAuthClient.signOut();
            drawerLayout.closeDrawer(GravityCompat.START);
            SendUserToLogin();
        }
        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        currentFragmentID = id;
    }


    //load fragment used for bottom nav view.
    public boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment).addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }


}