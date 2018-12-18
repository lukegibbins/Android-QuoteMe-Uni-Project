package com.example.quoteme;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.UserData.UserContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Double> latitudes = new ArrayList<>();
    private ArrayList<Double> longitudes = new ArrayList<>();
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> countries = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Stores all the cities and countries from the quotes table into parallel arrays
        getAllData();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Eventually put current location in here
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        plotLatAndLongMarkers();
    }

    //Plot markers for all lat and longs
    private void plotLatAndLongMarkers(){
        for(int i = 0; i < latitudes.size(); i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitudes.get(i), longitudes.get(i)))
            .title(cities.get(i)+", "+countries.get(i)));
        }
    }


    //Gets all cities and countries from quotes table
    private void getAllData() {
        String[] project = {"latitude, longitude, location_city, location_country"};
        Cursor cursor = getContentResolver().query(QuoteContract.QuoteEntry.CONTENT_URI,
                project,
                null,
                null,
                null
        );

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int latitudeColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LATITUDE);
            int longitudeColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LONGITUDE);
            int cityColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_CITY);
            int countryColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_COUNTRY);

            String latitudeString = cursor.getString(latitudeColumnIndex);
            String longitudeString = cursor.getString(longitudeColumnIndex);
            String cityString = cursor.getString(cityColumnIndex);
            String countryString = cursor.getString(countryColumnIndex);

            if(!latitudeString.isEmpty() && !longitudeString.isEmpty()) {
                double latitude = Double.parseDouble(latitudeString);
                double longitude = Double.parseDouble(longitudeString);

                latitudes.add(latitude);
                longitudes.add(longitude);
                cities.add(cityString);
                countries.add(countryString);
            }
        }
    }
}
