package com.example.sukrit.driverdrowsinessalertsystem.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.sukrit.driverdrowsinessalertsystem.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class PlaceSelectionActivity extends AppCompatActivity {

    Button btnAddRide;
    Double startLat,startLng,endLat,endLng;
    String source,destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_selection);

        btnAddRide = findViewById(R.id.btnAddRide);

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


        btnAddRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceSelectionActivity.this,AddRideActivity.class);
                intent.putExtra("startLat",startLat).putExtra("startLng",startLng)
                        .putExtra("endLat",endLat).putExtra("endLng",endLng)
                        .putExtra("src",source).putExtra("dest",destination);
                startActivity(intent);
            }
        });
    }
}
