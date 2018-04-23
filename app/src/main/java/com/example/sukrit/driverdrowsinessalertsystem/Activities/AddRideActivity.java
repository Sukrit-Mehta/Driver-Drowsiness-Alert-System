package com.example.sukrit.driverdrowsinessalertsystem.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.sukrit.driverdrowsinessalertsystem.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class AddRideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);
        //Places API Autocomplete

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
                Log.i("TAG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });
    }


}
