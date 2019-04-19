package com.example.sukrit.driverdrowsinessalertsystem.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.sukrit.driverdrowsinessalertsystem.Adapters.UsersRidesAdapter;
import com.example.sukrit.driverdrowsinessalertsystem.FirebaseServiceAlert;
import com.example.sukrit.driverdrowsinessalertsystem.Models.DriverCurrentRide;
import com.example.sukrit.driverdrowsinessalertsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView rvUserRides;
    ArrayList<DriverCurrentRide> arrayList;
    DatabaseReference mDatabasePastTrips;
    Context context;
    RelativeLayout rl1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        context = this;
        rvUserRides = findViewById(R.id.rvUserRides);
        rl1 = findViewById(R.id.rl1);

        arrayList =new ArrayList<>();
        mDatabasePastTrips= FirebaseDatabase.getInstance().getReference().child("CurrentRides");

        mDatabasePastTrips.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot needSnapshot : dataSnapshot.getChildren())
                {
                    DriverCurrentRide ride = needSnapshot.getValue(DriverCurrentRide.class);
                    arrayList.add(ride);
                }
                Collections.reverse(arrayList);
                if(arrayList.size()>0){
                    rvUserRides.setVisibility(View.VISIBLE);
                    rl1.setVisibility(View.GONE);
                    UsersRidesAdapter usersRidesAdapter= new UsersRidesAdapter(arrayList,context);
                    rvUserRides.setAdapter(usersRidesAdapter);
                }
                else {
                    rvUserRides.setVisibility(View.GONE);
                    rl1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        rvUserRides.setLayoutManager(new LinearLayoutManager(this));
        rvUserRides.setAdapter(new UsersRidesAdapter(arrayList,this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // final UserRide demoRide = new UserRide("Meerut","Noida","1234","12:40","14:20","24-10-1997",21.1,55.2,22.3,77.8,2.3,false);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabasePastTrips.push().setValue(demoRide).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(context, "Data sent to Server.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failure..!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.add));
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        startService(new Intent(this, FirebaseServiceAlert.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
