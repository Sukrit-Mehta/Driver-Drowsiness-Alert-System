package com.example.sukrit.driverdrowsinessalertsystem.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.sukrit.driverdrowsinessalertsystem.Models.UserRide;
import com.example.sukrit.driverdrowsinessalertsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    Switch sw_driver_Rider;
    EditText etname, etemail, etmobno, etpassword, etVehicleno;
    Button btnSubmit;
    Boolean flag = false;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        progressDialog = new ProgressDialog(this);

        sw_driver_Rider = (Switch) findViewById(R.id.sw_Driver_Rider);
        etemail = (EditText) findViewById(R.id.et_signUpEmail);
        etmobno = findViewById(R.id.et_signUpmobile_no);
        etname = findViewById(R.id.et_signUpName);
        etpassword = findViewById(R.id.et_signUpPassword);
        etVehicleno = findViewById(R.id.et_SignUpVehicleNo);
        btnSubmit = findViewById(R.id.btn_signUp);
        sw_driver_Rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_driver_Rider.isChecked()) {
                    flag = true;
                    etVehicleno.setVisibility(View.GONE);
                } else {
                    flag = false;
                    etVehicleno.setVisibility(View.VISIBLE);
                }
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValidateFields())
                {
                    String Name,Mail,mobno,password,VehicleNo;
                    Name = etname.getText().toString();
                    Mail = etemail.getText().toString();
                    mobno = etmobno.getText().toString();
                    password = etpassword.getText().toString();
                    VehicleNo = etVehicleno.getText().toString();
                    if(flag==true)
                    {
                        progressDialog.setMessage("Registering Rider...");
                        progressDialog.show();
                        // User is a Rider
                        final Rider thisRider = new Rider(Name,Mail,password,mobno);
                        firebaseAuth.createUserWithEmailAndPassword(Mail,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            databaseReference.child("Riders").push().setValue(thisRider)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressDialog.dismiss();
                                                            if(task.isSuccessful())
                                                            {

                                                                Intent thisIntent = new Intent(SignupActivity.this,UserActivity.class);
                                                                Toast.makeText(SignupActivity.this, "Rider Registered", Toast.LENGTH_SHORT).show();
                                                                startActivity(thisIntent);
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(SignupActivity.this, "Error in Registering!.. Please Try Again", Toast.LENGTH_SHORT).show();
                                                                firebaseAuth.getCurrentUser().delete();
                                                            }
                                                        }
                                                    });
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignupActivity.this, "Error in Registering!.. Please Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                    else
                    {
                        progressDialog.setMessage("Registering Driver...");
                        progressDialog.show();
                        // user is Driver
                        final Driver thisDriver = new Driver(Name,Mail,VehicleNo,password,mobno,null,null);
                        firebaseAuth.createUserWithEmailAndPassword(Mail,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            databaseReference.child("Drivers").push().setValue(thisDriver)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressDialog.dismiss();
                                                            if(task.isSuccessful())
                                                            {
                                                                Intent thisIntent = new Intent(SignupActivity.this,DriverActivity.class);
                                                                Toast.makeText(SignupActivity.this, "Driver Registered", Toast.LENGTH_SHORT).show();
                                                                startActivity(thisIntent);
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(SignupActivity.this, "Error in Registering!.. Please Try Again", Toast.LENGTH_SHORT).show();
                                                                firebaseAuth.getCurrentUser().delete();
                                                            }
                                                        }
                                                    });
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignupActivity.this, "Error in Registering!.. Please Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
            }
        });

    }

    public Boolean ValidateFields()
    {

        String Name,Mail,mobno,password,VehicleNo;
        Name = etname.getText().toString();
        Mail = etemail.getText().toString();
        mobno = etmobno.getText().toString();
        password = etpassword.getText().toString();
        VehicleNo = etVehicleno.getText().toString();

        if(Name.trim().length()==0)
        {
            etname.setError("Name is Required");
            return false;
        }
        if(Mail.trim().length()==0)
        {
            etname.setError("Mail id is Required");
            return false;
        }
        if(mobno.trim().length()==0 || mobno.trim().length()!=10)
        {
            etname.setError("Mobile No is Required");
            return false;
        }
        if(password.trim().length()==0)
        {
            etname.setError("Password is Required");
            return false;
        }
        if(VehicleNo.trim().length()==0 && flag==false)
        {
            etname.setError("Vehicle No. is Required");
            return false;
        }
        else
        {
            return  true;
        }
    }

}
