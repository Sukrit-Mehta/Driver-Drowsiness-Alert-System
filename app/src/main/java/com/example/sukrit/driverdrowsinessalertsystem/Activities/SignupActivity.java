package com.example.sukrit.driverdrowsinessalertsystem.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Switch;

import com.example.sukrit.driverdrowsinessalertsystem.R;

public class SignupActivity extends AppCompatActivity {

    Switch sw_driver_Rider;
    EditText etname,etemail,etmobno,etpassword,etVehicleno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sw_driver_Rider = (Switch) findViewById(R.id.sw_Driver_Rider);
        etemail = (EditText)findViewById(R.id.et_signUpEmail);
        etmobno = findViewById(R.id.et_signUpmobile_no);
        etname = findViewById(R.id.et_signUpName);
        etpassword = findViewById(R.id.et_signUpPassword);




    }
}
