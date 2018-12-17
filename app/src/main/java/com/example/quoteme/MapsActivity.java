package com.example.quoteme;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitude;
    double longitude;

    List<Address> geocodeMatches = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        getAllQuoteLocations();

    }

    private void getAllQuoteLocations(){
        try {
            geocodeMatches = new Geocoder(this).getFromLocationName(
                    "Sunderland, England", 1);
        } catch (IOException e){
            System.out.println("*************");
            e.printStackTrace();
        }

        if(!geocodeMatches.isEmpty())
        {
            latitude = geocodeMatches.get(0).getLatitude();
            longitude = geocodeMatches.get(0).getLongitude();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Sunderland"));
        }
    }
}
