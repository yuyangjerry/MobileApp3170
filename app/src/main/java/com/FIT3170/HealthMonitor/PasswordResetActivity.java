package com.FIT3170.HealthMonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        TextInputLayout emailLayout = findViewById(R.id.reset_email_text_field_input);
        Button b = findViewById(R.id.reset_password_button);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        b.setOnClickListener(l -> {
            String email = emailLayout.getEditText().getText().toString();
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener( task -> {
                        if(task.isSuccessful()){
                            Intent i = new Intent(this, PasswordResetConfirmationActivity.class);
                            i.putExtra("email", email);
                            startActivity(i);
                        }else{
                            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                        }
                    } );
        });
    }
}