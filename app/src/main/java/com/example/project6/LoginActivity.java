package com.example.project6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project6.Database.DatabaseTest;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText pswdEditText;
    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getComponentIDs();
    }

    private void getComponentIDs(){
        emailEditText = findViewById(R.id.email_input);
        pswdEditText = findViewById(R.id.password_edit_text);
        signInBtn = findViewById(R.id.sign_in_button);

        signInBtn.setOnClickListener(view -> {

           login(emailEditText.getText().toString(), pswdEditText.getText().toString());

        });
    }
    private void login(String email, String password){
        Context context = this;
        FireBaseAuthClient.signIn(email, password, new SignInConsumer(){

            @Override
            public void onSigninSuccess(FirebaseUser user) {
                UpdateUI();
                Toast.makeText(context,  "Welcome", Toast.LENGTH_SHORT).show();

                // FOR TESTING POSTING TO DATABASE
                /*
                try {
                    new DatabaseTest().testAddNewPatient();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                 */

                new DatabaseTest().testAddReading();
            }

            @Override
            public void onSigninFailure() {
                Toast.makeText(context,  "Invalid Login", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateUI(){

    }



}