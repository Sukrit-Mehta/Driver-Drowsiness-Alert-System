package com.example.sukrit.driverdrowsinessalertsystem.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.sukrit.driverdrowsinessalertsystem.Models.Driver;
import com.example.sukrit.driverdrowsinessalertsystem.Models.Rider;
import com.example.sukrit.driverdrowsinessalertsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    EditText etEmail,etPassword;
    Button btnSubmit;
    Switch sw_LoginRider_Driver;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    SharedPreferences sharedPref;
    public static final String MyTag = "User";
    SharedPreferences.Editor editor;

    Boolean flag=false;
    //If flag true means Rider and flag false means driver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging you in!...");

        sharedPref = getSharedPreferences(MyTag,MODE_PRIVATE);
        editor = sharedPref.edit();

        etEmail = findViewById(R.id.login_email);
        etPassword = findViewById(R.id.login_password);
        btnSubmit = findViewById(R.id.btn_Login_Submit);
        sw_LoginRider_Driver = findViewById(R.id.sw_LoginDriver_Rider);

        sw_LoginRider_Driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sw_LoginRider_Driver.isChecked())
                {
                    flag = true;
                }
                else
                {
                    flag = false;
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Email = etEmail.getText().toString();
                String Password = etPassword.getText().toString();
                if(ValidateFields())
                {
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(Email,Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful())
                                    {


                                        ChildEventListener DriverCheckListner = new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                Driver thisDriver = dataSnapshot.getValue(Driver.class);
                                                if(thisDriver.getEmail().equals(Email))
                                                {
                                                    //Driver Found in the database
                                                    Intent thisIntent = new Intent(LoginActivity.this,DriverActivity.class);

                                                    startActivity(thisIntent);
                                                }

                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        };

                                        ChildEventListener RiderEventListner = new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                Rider thisRider = dataSnapshot.getValue(Rider.class);
                                                if(thisRider.getEmail().equals(Email))
                                                {
                                                    //Rider Found
                                                    Intent thisIntent = new Intent(LoginActivity.this,UserActivity.class);
                                                    startActivity(thisIntent);
                                                }
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        };

                                        Toast.makeText(LoginActivity.this, "Login Sucessfull!...", Toast.LENGTH_SHORT).show();
                                        //TODO : Send an intent to the other activity
                                    }

                                    else
                                    {
                                        Toast.makeText(LoginActivity.this, "Login Failed!.. Username or Password Incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                }
            }
        });


    }

    public Boolean ValidateFields()
    {
        String Email = etEmail.getText().toString();
        String Password = etPassword.getText().toString();

        if(Email.trim().length()==0 || !Email.contains("@"))
        {
            etEmail.setError("Please Provide a valid Email!..");
            return  false;
        }
        else if(Password.length()==0 )
        {
            etPassword.setError("Please Enter your Paswsword!...");
            return false;
        }
        else
        {
            return true;
        }
    }
}
