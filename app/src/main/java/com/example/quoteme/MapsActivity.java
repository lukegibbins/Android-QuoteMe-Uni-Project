package com.example.quoteme;


import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import static com.example.quoteme.PremiumAccessActivity.LAT_KEY;
import static com.example.quoteme.PremiumAccessActivity.LONG_KEY;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String latFromPostcode;
    private String longFromPostcode;

    private ArrayList<Double> latitudes = new ArrayList<>();
    private ArrayList<Double> longitudes = new ArrayList<>();
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> countries = new ArrayList<>();
    private ArrayList<String> vendors = new ArrayList<>();
    private ArrayList<String> quoteTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //set current location using postcode from previous page
        //this location can also be used check distance between quotes
        latFromPostcode = getIntent().getStringExtra(LAT_KEY);
        longFromPostcode = getIntent().getStringExtra(LONG_KEY);

        //Stores all the cities and countries from the quotes table into parallel arrays
        try {
            getAllData();
        } catch(Exception e){
            //Continue with the locations that do have lat and longs
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        String address1 = null;
        mMap = googleMap;
        plotLatAndLongMarkers();

        Double latDouble = Double.valueOf(latFromPostcode);
        Double longDouble = Double.valueOf(longFromPostcode);

        List<Address> geocodeMatches = null;
        try {
            geocodeMatches = new Geocoder(this).getFromLocation(latDouble, longDouble, 1);
        }catch(IOException e){
            e.printStackTrace();
        }

        if(!geocodeMatches.isEmpty()){
             address1 = geocodeMatches.get(0).getAddressLine(0);
        }

        try {
            // Add a marker for users current location and move the camera
            LatLng currentLocationFromPostcode = new LatLng(latDouble, longDouble);
            mMap.addMarker(new MarkerOptions().position(currentLocationFromPostcode).title("Your Location: " +
                    address1));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocationFromPostcode));
        } catch (Exception e){
            Toasty.error(this,"Error setting current location", Toast.LENGTH_SHORT).show();
        }
    }

    //Plot markers for all lat and longs
    private void plotLatAndLongMarkers(){
        for(int i = 0; i < quoteTitles.size(); i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitudes.get(i), longitudes.get(i)))
            .title(cities.get(i)+", "+countries.get(i))).setSnippet("Vendor: " + vendors.get(i) + " | " +
                    "Title: "+ quoteTitles.get(i) +  " | Status: "+ " Pending");
        }
    }

    //Gets all cities and countries from quotes table
    private void getAllData() {

        String selection = "status=?";
        String [] selectionArgs = {"0"};
        String[] project = {"latitude, longitude, location_city, location_country, vendor, title"};
        Cursor cursor = getContentResolver().query(QuoteContract.QuoteEntry.CONTENT_URI,
                project,
                selection,
                selectionArgs,
                null
        );

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int latitudeColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LATITUDE);
            int longitudeColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LONGITUDE);
            int cityColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_CITY);
            int countryColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_COUNTRY);
            int vendorColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR);
            int titleColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE);

            String latitudeString = cursor.getString(latitudeColumnIndex);
            String longitudeString = cursor.getString(longitudeColumnIndex);
            String cityString = cursor.getString(cityColumnIndex);
            String countryString = cursor.getString(countryColumnIndex);
            String vendorString = cursor.getString(vendorColumnIndex);
            String titleString = cursor.getString(titleColumnIndex);

            if(!latitudeString.equals(null) && !longitudeString.equals(null)) {
                double latitude = Double.parseDouble(latitudeString);
                double longitude = Double.parseDouble(longitudeString);

                latitudes.add(latitude);
                longitudes.add(longitude);
                cities.add(cityString);
                countries.add(countryString);
                vendors.add(vendorString);
                quoteTitles.add(titleString);
            }
        }
    }
}
