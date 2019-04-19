package com.example.sukrit.driverdrowsinessalertsystem.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sukrit.driverdrowsinessalertsystem.Models.Driver;
import com.example.sukrit.driverdrowsinessalertsystem.Models.DriverCurrentRide;
import com.example.sukrit.driverdrowsinessalertsystem.Models.ReturnObject;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
    public static final int MY_PERMISSION_REQUEST_CAMERA = 231;
    public static final int MY_PERMISSION_REQUEST_STORAGE = 222;
    private boolean permissionIsGranted = false;
    public static final Integer REQUEST_CHECK_SETTINGS = 888;
    ProgressDialog progressDialog;
    DatabaseReference mCurrentRides,mPastRides;
    Button btnRequestPickup;
    DriverCurrentRide driverCurrentRide;
    String source,destination,driverID,startTime,endTime,date,vehicleNo,imageStr="";
    Integer sleepCount=0;
    Double avgSpeed,startLat,startLng,endLat,endLng,rating,currentLat,currentLng;
    Boolean isMoving = false;
    Boolean rideAlive = false;
    RequestQueue requestQueue;
    Bitmap bitmap;
    ArrayList<Double> earArrayList;

    SharedPreferences sharedPref;
    public static final String MyTag = "User";
    SharedPreferences.Editor editor;

    Driver thisDriver;
    Gson gson;

    SurfaceView svCamera;
    SurfaceHolder surfaceHolder;
    Camera camera;
    Camera.PictureCallback picCallBack;
    DatabaseReference dbIsSleeping, dbIsDrowsy ;
    int totalCount = 0, drowsyCount = 0 ;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);

        earArrayList = new ArrayList<>();
        gson = new Gson();
        dbIsSleeping = FirebaseDatabase.getInstance().getReference().child("isSleeping");
        dbIsDrowsy = FirebaseDatabase.getInstance().getReference().child("isDrowsy");
        startLat = getIntent().getDoubleExtra("startLat",0.0);
        startLng = getIntent().getDoubleExtra("startLng",0.0);
        endLat = getIntent().getDoubleExtra("endLat",0.0);
        endLng = getIntent().getDoubleExtra("endLng",0.0);
        source = getIntent().getStringExtra("src");
        destination = getIntent().getStringExtra("dest");
        requestQueue= Volley.newRequestQueue(this);

        svCamera = (SurfaceView) findViewById(R.id.surfaceViewCamera);


        sharedPref = getSharedPreferences(MyTag,MODE_PRIVATE);
        thisDriver =  gson.fromJson(sharedPref.getString("User",""),Driver.class);

        checkCameraSizes();

        picCallBack = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                String fileName = "photo-" + System.currentTimeMillis() + ".jpg";
                File myPhoto = new File(Environment.getExternalStorageDirectory(), fileName);
                Log.d(TAG, "onPictureTaken: myPhoto: "+myPhoto);
                Log.d(TAG, "onPictureTaken: Uri: "+ getImageContentUri(AddRideActivity.this,myPhoto));

                try {
                    FileOutputStream fos = new FileOutputStream(myPhoto);
                    fos.write(data);
                    fos.close();
                    Uri myImageUri =getImageContentUri(AddRideActivity.this,myPhoto);
                    String s = getPath(AddRideActivity.this,myImageUri);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), myImageUri);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap tempBitmap = MyBitmapCompressor.getCompressedImage(s,256,200);
                        Bitmap myBitmap = rotateImage(tempBitmap,270);
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 2, stream); //compress to which format you want.
                         byte[] byte_arr = stream.toByteArray();
                        imageStr = Base64.encodeToString(byte_arr,Base64.DEFAULT);
                        Log.d(TAG, "onPictureTaken: imgStr = " +imageStr);
                        camera.startPreview();
                        volleyFunction();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {


                } catch (Exception e) {
                    Log.d(TAG, "onPictureTaken: Could not restart preview", e);
                }
            }
        };

        //Places API Autocomplete

        mCurrentRides = FirebaseDatabase.getInstance().getReference().child("CurrentRides");
        mPastRides = FirebaseDatabase.getInstance().getReference().child("PastRides");
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
        date = sdf.format(new Date());

        vehicleNo=thisDriver.getVehicleNo();


        rating = 4.8;

        btnRequestPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnRequestPickup.getTag().equals("addRide"))
                {
                    btnRequestPickup.setTag("startRide");
                    btnRequestPickup.setText("Start Ride");
                    btnRequestPickup.setBackgroundColor(Color.parseColor("#5ed83c"));
                   /* driverCurrentRide=new DriverCurrentRide(source,destination,driverID,
                            sleepCount,avgSpeed,startLat,
                            startLng,endLat,endLng,startTime,
                            endTime,date,rating, currentLat,
                            currentLng,vehicleNo,isMoving);*/
/*
                    mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(driverCurrentRide);
*/
                }
                else if(btnRequestPickup.getTag().equals("startRide")){
                    capturePhoto();
                    rideAlive = true;
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
                    Log.d(TAG, "onClick: "+driverCurrentRide.getSource()+","
                    +driverCurrentRide.getDestination());
                    mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(driverCurrentRide);
                    mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("isMoving").setValue(isMoving);
                }
                else {
                    rideAlive = false;
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
                    mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("endTime").setValue( Calendar.getInstance().getTime().toString());
                    dbIsSleeping.child("sleep").setValue(false);
                    dbIsDrowsy.child("drowsy").setValue(false);
                    driverCurrentRide.setEndTime(Calendar.getInstance().getTime().toString());
                    mPastRides.push().setValue(driverCurrentRide);

                    mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();


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
        if (camera != null) {
            try {
                camera.reconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (permissionIsGranted) {
            if (googleApiClient.isConnected()) {
                requestLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.stopPreview();
        }
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
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();
        avgSpeed = Double.valueOf(location.getSpeed());
        ValueEventListener thisListner  = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    if(rideAlive)
                    {
                        mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("currentLat").setValue(currentLat);
                        mCurrentRides.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("currentLng").setValue(currentLng);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if(rideAlive)
        {
            mCurrentRides.addValueEventListener(thisListner);
        }
        else
        {
            if(thisListner!=null)
            {

                mCurrentRides.removeEventListener(thisListner);
            }

        }


        Log.d(TAG, "onLocationChanged: "+location);
    }



    void capturePhoto() {

        camera.takePicture(null, null, picCallBack);

    }

    void checkCameraSizes () {
        camera = Camera.open(1);

        Log.d(TAG, "checkCameraSizes:  open ");

        final Camera.Parameters camParams = camera.getParameters();
        for (Camera.Size picSize : camParams.getSupportedPictureSizes()) {
            Log.d(TAG, "picSize: " + picSize.width + " " + picSize.height);
        }
        for (Camera.Size prevSize : camParams.getSupportedPreviewSizes()) {
            Log.d(TAG, "prevSize: " + prevSize.width + " " + prevSize.height);
        }
        for (Camera.Size vidSize : camParams.getSupportedVideoSizes()) {
            Log.d(TAG, "vidSize: " + vidSize.width + " " + vidSize.height);
        }

        surfaceHolder = svCamera.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.setDisplayOrientation(90);
                    camera.startPreview();
                    Log.d(TAG, "surfaceCreated: ");
                } catch (IOException e) {
                    Log.d(TAG, "surfaceCreated: Could not start preview" );
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                WindowManager winMan = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = winMan.getDefaultDisplay();

                if (holder.getSurface() == null) {
                    return;
                }

                try {
                    camera.stopPreview();
                } catch (Exception e) {
                    Log.e(TAG, "surfaceChanged: ", e);
                }

                Camera.Parameters changedParams = camera.getParameters();

                if (display.getRotation() == Surface.ROTATION_0) {
                    camera.setDisplayOrientation(90);

                }
                if (display.getRotation() == Surface.ROTATION_90) {
                    camera.setDisplayOrientation(0);

                }
                if (display.getRotation() == Surface.ROTATION_180) {
                    camera.setDisplayOrientation(270);


                }
                if (display.getRotation() == Surface.ROTATION_270) {
                    camera.setDisplayOrientation(180);
                }

                try {
                    camera.setParameters(changedParams);
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "surfaceChanged: ", e);
                }
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

    }
//
//    public void volleyFunction(){
//
//        Map<String,String> params=new HashMap<>();
//        params.put("image",imageStr);
//        params.put("count","1");
//        params.put("personName","bjhdbce");
//        JSONObject j = new JSONObject(params);
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://192.168.43.187:5002/",
//                j, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, "return_list: "+response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
////        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.43.163:5001/", new com.android.volley.Response.Listener<String>() {
////        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://192.168.43.187:5002/", new com.android.volley.Response.Listener<String>() {
////            @Override
////            public void onResponse(String response) {
////                capturePhoto();
////                Log.d(TAG, "return_list: "+response);
////            }
////        }, new com.android.volley.Response.ErrorListener() {
////            @Override
////            public void onErrorResponse(VolleyError error) {
////                Toast.makeText(AddRideActivity.this, "Face not visible..!", Toast.LENGTH_SHORT).show();
////                Log.d(TAG, "onErrorResponse: "+error);
////                //capturePhoto();
////            }
////        }){
////            @Override
////            protected Map<String, String> getParams() throws AuthFailureError {
////
////                Map<String,String> params=new HashMap<>();
////
////                params.put("image",imageStr);
////                params.put("count","1");
////                params.put("personName","bjhdbce");
////                Log.d(TAG, "getParams: " +params);
////                return params;
////            }
////        };
//        request.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 50000;
//            }
//
//            @Override
//            public int getCurrentRetryCount() {
//                return 50000;
//            }
//
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//
//            }
//        });
//        requestQueue.add(request);
//    }



    public void volleyFunction(){
//        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.43.163:5001/", new com.android.volley.Response.Listener<String>() {
          StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.43.187:5002/", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                capturePhoto();
//                if(response!=null)
//                if(Double.parseDouble(response)<0.25)
//                    sleepCount++;
//                else {
//                    if(sleepCount>0)
//                        sleepCount=0;
//                }
//                perclosFunction(Double.parseDouble(response));
//                Log.d(TAG, "onResponse: sleepCount = "+sleepCount);
//                if(sleepCount>=3)
//                    dbIsSleeping.child("sleep").setValue(true);
                 Gson gson = new Gson();
                 ReturnObject returnObject  = gson.fromJson(response, ReturnObject.class);
                Log.d(TAG, "return_list: Ear =  "+returnObject.getEar()+"\n"+
                        "Mor =  "+returnObject.getMor()+"\n"+
                        "Nlr =  "+returnObject.getNlr()+"\n");

                if(response!=null)
                if(Double.parseDouble(returnObject.getEar())<0.25)
                    sleepCount++;
                else {
                    if(sleepCount>0)
                        sleepCount=0;
                }
                Log.d(TAG, "onResponse: sleepCount = "+sleepCount);
                if(sleepCount>=2 || (Double.parseDouble(returnObject.getMor())>0.35)
                                 || (Double.parseDouble(returnObject.getNlr())<40))
                    dbIsSleeping.child("sleep").setValue(true);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddRideActivity.this, "Face not visible..!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse: "+error);
                //capturePhoto();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>();

                params.put("image",imageStr);
                params.put("count","1");
                params.put("personName","bjhdbce");
                Log.d(TAG, "getParams: " +params);
                return params;
            }
        };
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(request);
    }

    public void perclosFunction(Double ear){
        totalCount++;
        earArrayList.add(ear);
        if(ear<0.25){
            drowsyCount++;
           // Log.d(TAG, "perclosFunction: "+drowsyCount);
        }
        if(totalCount == 14){
            Double percent = (drowsyCount * 1.0/totalCount)*100.0;
            Log.d(TAG, "EyeClosureRatio: " + percent);
            if(percent>50){
                // Trigger firebase for the state of drowsiness only..!
                dbIsDrowsy.child("drowsy").setValue(true);
            }
            totalCount = 0;
            drowsyCount = 0;
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Log.d(TAG, "getImageContentUri: filePath: "+filePath);
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "getImageContentUri: Cursor");
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            //if (imageFile.exists()) {
            Log.d(TAG, "getImageContentUri: Exists");
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
           /* } else {
                Log.d(TAG, "getImageContentUri: no Existence");
                return null;
            }*/
        }
    }
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
