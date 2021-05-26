package com.FIT3170.HealthMonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private TextInputLayout pswdLayout;
    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getComponentIDs();
    }

    private void getComponentIDs(){
        emailLayout = findViewById(R.id.email_input);
        pswdLayout = findViewById(R.id.password_edit_text);
        signInBtn = findViewById(R.id.sign_in_button);

        signInBtn.setOnClickListener(view -> {

           login(emailLayout.getEditText().getText().toString(), pswdLayout.getEditText().getText().toString());

        });
    }
    private void login(String email, String password){
        Context context = this;
        pswdLayout.setError("");
        FireBaseAuthClient.signIn(email, password, new SignInConsumer(){

            @Override
            public void onSigninSuccess(FirebaseUser user) {
                UpdateUI();
                Toast.makeText(context,  "Welcome", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigninFailure() {
                pswdLayout.setError("Email or password do not match");
            }
        });
    }

    private void UpdateUI(){
        Intent intent = new Intent(this, UserProfile.class);
        startActivity(intent);
    }
}