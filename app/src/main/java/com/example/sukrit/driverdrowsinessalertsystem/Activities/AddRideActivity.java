package com.example.sukrit.driverdrowsinessalertsystem.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sukrit.driverdrowsinessalertsystem.Models.DriverCurrentRide;
import com.example.sukrit.driverdrowsinessalertsystem.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddRideActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = "SUKKI";

    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Double myLatitude;
    private Double myLongitude;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 102;
    private boolean permissionIsGranted = false;
    public static final Integer REQUEST_CHECK_SETTINGS = 888;
    ProgressDialog progressDialog;
    DatabaseReference mCurrentRides;
    Button btnRequestPickup;
    DriverCurrentRide driverCurrentRide;
    String source,destination,driverID,startTime,endTime,date,vehicleNo;
    Integer sleepCount;
    Double avgSpeed,startLat,startLng,endLat,endLng,rating,currentLat,currentLng;
    Boolean isMoving = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);
        //Places API Autocomplete

        mCurrentRides = FirebaseDatabase.getInstance().getReference().child("CurrentRides");
        btnRequestPickup = findViewById(R.id.btnRequestPickup);
        Log.d(TAG, "onCreate: Add Ride");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        progressDialog = new ProgressDialog(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("SUH", "All location settings are satisfied.");

                        progressDialog.dismiss();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("SUH", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
//                                progressDialog.dismiss();
                            status.startResolutionForResult(AddRideActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("SUH", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("SUH", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        Toast.makeText(AddRideActivity.this, "Turn on Location", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        break;
                }
            }
        });

        driverID = FirebaseAuth.getInstance().getUid();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        date = sdf.format(new Date());
        vehicleNo="UP-15 AW-6258";


        rating = 4.8;
        sleepCount=20;

        PlaceAutocompleteFragment autocompleteSourceFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_source);

        PlaceAutocompleteFragment autocompleteDestinationFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_destination);

        autocompleteSourceFragment.setHint("Source");
        autocompleteDestinationFragment.setHint("Destination");

        autocompleteSourceFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                source = place.getName().toString();
                startLat = place.getLatLng().latitude;
                startLng = place.getLatLng().longitude;
                Log.i("TAG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        autocompleteDestinationFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place.getName().toString();
                endLat = place.getLatLng().latitude;
                endLng = place.getLatLng().longitude;
                Log.i("TAG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        btnRequestPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnRequestPickup.getTag().equals("addRide"))
                {
                    btnRequestPickup.setTag("startRide");
                    btnRequestPickup.setText("Start Ride");
                    btnRequestPickup.setBackgroundColor(Color.parseColor("#5ed83c"));
                }
                else if(btnRequestPickup.getTag().equals("startRide")){
                    btnRequestPickup.setTag("stopRide");
                    btnRequestPickup.setText("Stop Ride");
                    btnRequestPickup.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                    startTime = Calendar.getInstance().getTime().toString();
                    isMoving = true;

                    driverCurrentRide=new DriverCurrentRide(source,destination,driverID,
                            sleepCount,avgSpeed,startLat,
                            startLng,endLat,endLng,startTime,
                            endTime,date,rating, currentLat,
                            currentLng,vehicleNo,isMoving);
                    mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(driverCurrentRide);
                }
                else {
                    btnRequestPickup.setTag("addRide");
                    btnRequestPickup.setText("Add Ride");
                    btnRequestPickup.setBackgroundColor(Color.parseColor("#41a6f4"));
                    endTime = Calendar.getInstance().getTime().toString();
                    isMoving = false;
                    mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("endLat").setValue(currentLat);
                    mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("endLng").setValue(currentLng);
                    mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("isMoving").setValue(false);
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                permissionIsGranted = true;
            }
            return;
        }
        locationRequest.setInterval(2000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionIsGranted) {
            if (googleApiClient.isConnected()) {
                requestLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (permissionIsGranted)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (permissionIsGranted)
            googleApiClient.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    permissionIsGranted = true;
                } else {
                    //permission denied
                    permissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), "This app requires location permission to be granted", Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_PERMISSION_REQUEST_COARSE_LOCATION:
                // do something for coarse location
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();
        avgSpeed = Double.valueOf(location.getSpeed());
        mCurrentRides.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("currentLat").setValue(currentLat);
                            mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("currentLng").setValue(currentLng);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        Log.d(TAG, "onLocationChanged: "+location);
    }
}
