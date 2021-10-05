package com.FIT3170.HealthMonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class PasswordResetConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_confirmation);

        Button b = findViewById(R.id.back_to_login_button);
        Intent receivedIntent = getIntent();
        String email = receivedIntent.getStringExtra("email");

        b.setOnClickListener(l -> {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });
    }
}