package com.example.sukrit.driverdrowsinessalertsystem;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sharaddadhich on 07/05/18.
 */

public class FirebaseServiceAlert extends Service {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERI", "onCreate: ");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SERI", "onStartCommand: " + "In Start Command");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("isSleeping");
        Log.d("SERI", "onStartCommand: " + databaseReference.toString());
        ListenerForRequestDone();
        return START_STICKY;

    }

    public void ListenerForRequestDone(){
        Log.d("SERI", "ListenerForRequestDone: in Function" );
        Log.d("fun ref", "ListenerForRequestDone: " + databaseReference.toString());
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("SERI", "onChildChanged: ");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("SERI", "onChildChanged: "+dataSnapshot.getValue());
                if(dataSnapshot.getValue().toString().equalsIgnoreCase("true")) {
                    Intent gotomain = new Intent();
                    gotomain.setClassName("com.example.sukrit.driverdrowsinessalertsystem", "com.example.sukrit.driverdrowsinessalertsystem.Activities.DriverSleepAlertActivity");
                    gotomain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(gotomain);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("SERI", "onChildChanged: ");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("SERI", "onChildChanged: ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("SERI", "onChildChanged: ");
            }
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

}
