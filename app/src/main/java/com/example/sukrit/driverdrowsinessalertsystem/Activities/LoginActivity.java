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
import android.widget.TextView;
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
import com.google.gson.Gson;


public class LoginActivity extends AppCompatActivity {

    EditText etEmail,etPassword;
    Button btnSubmit;
    Switch sw_LoginRider_Driver;

    TextView tvSignUp;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    SharedPreferences sharedPref;
    public static final String MyTag = "User";
    SharedPreferences.Editor editor;
    ChildEventListener RiderEventListner;
    ChildEventListener DriverCheckListner;

    Gson gson;

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

        if(sharedPref.getBoolean("Driver",false))
        {
            Intent thisIntent = new Intent(LoginActivity.this,DriverActivity.class);
            startActivity(thisIntent);
        }
        if(sharedPref.getBoolean("Rider",false))
        {
            Intent thisIntent = new Intent(LoginActivity.this,UserActivity.class);
            startActivity(thisIntent);
        }



        etEmail = findViewById(R.id.login_email);
        etPassword = findViewById(R.id.login_password);
        btnSubmit = findViewById(R.id.btn_Login_Submit);
        sw_LoginRider_Driver = findViewById(R.id.sw_LoginDriver_Rider);
        tvSignUp = findViewById(R.id.signUp);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(newIntent);
            }
        });

        gson = new Gson();

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





                                        DriverCheckListner = new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                Driver thisDriver = dataSnapshot.getValue(Driver.class);
                                                if(thisDriver.getEmail().equals(Email))
                                                {
                                                    //Driver Found in the database
                                                    Intent thisIntent = new Intent(LoginActivity.this,DriverActivity.class);
                                                    String thisData = gson.toJson(thisDriver);
                                                    editor.putString("User",thisData);
                                                    editor.putBoolean("Driver",true);
                                                    editor.putBoolean("Rider",false);
                                                    editor.commit();
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


                                        RiderEventListner = new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                Rider thisRider = dataSnapshot.getValue(Rider.class);
                                                if(thisRider.getEmail().equals(Email))
                                                {
                                                    //Rider Found
                                                    Intent thisIntent = new Intent(LoginActivity.this,UserActivity.class);
                                                    String thisData = gson.toJson(thisRider);
                                                    editor.putString("User",thisData);
                                                    editor.putBoolean("Rider",true);
                                                    editor.putBoolean("Driver",false);
                                                    editor.commit();
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


                                        if(flag)
                                        {
                                            databaseReference.child("Riders").addChildEventListener(RiderEventListner);
                                        }
                                        else
                                        {
                                            databaseReference.child("Drivers").addChildEventListener(DriverCheckListner);
                                        }

                                        Toast.makeText(LoginActivity.this, "Login Sucessfull!...", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onPause() {
        super.onPause();
        if(DriverCheckListner!=null)
        {
            databaseReference.child("Drivers").removeEventListener(DriverCheckListner);
        }
        if(RiderEventListner!=null)
        {
            databaseReference.child("Riders").removeEventListener(RiderEventListner);
        }


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
