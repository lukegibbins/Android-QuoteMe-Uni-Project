package com.example.quoteme;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.quoteme.Models.MapObject;
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
    private ArrayList<MapObject> mapObjects = new ArrayList<>();
    private ArrayList<MapObject> mapObjectsWithinLocation = new ArrayList<>();

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

        //Stores all the cities and countries from the quotes table
        //then filters the data depending on the users distance parameter
        try {
            getAllData();
            filterMapDataByUsersLocation();
        } catch(Exception e){
            //Continue with the locations that do have lat and longs
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String address1 = null;
        mMap = googleMap;
        plotLatAndLongMarkersForFilteredData();

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
    private void plotLatAndLongMarkersForFilteredData(){
        for(int i = 0; i < mapObjectsWithinLocation.size(); i++){
            mMap.addMarker(new MarkerOptions().position(mapObjects.get(i).getLatLng())
            .title(mapObjects.get(i).getCity()+", "+mapObjects.get(i).getCountry()))
                    .setSnippet("Vendor: " + mapObjects.get(i).getVendor() + " | " +
                    "Title: "+ mapObjects.get(i).getQuoteTitle() +  " | Status: "+ "Pending");
        }
    }

    private void filterMapDataByUsersLocation(){
        //Users current known location
        Double latDouble = Double.valueOf(latFromPostcode);
        Double longDouble = Double.valueOf(longFromPostcode);
        Location usersCurrentLocation = new Location("Users Location");
        usersCurrentLocation.setLatitude(latDouble);
        usersCurrentLocation.setLongitude(longDouble);

        //Current object in iteration
        Location mapObjectInIteration = new Location("Quote Location");

        //tempVar for catching distance
        Float totalDistance;

        //Users set parameter distance and conversion to float
        int userDistance = 855000;

        for(int i = 0; i < mapObjects.size(); i++){
            mapObjectInIteration.setLatitude(mapObjects.get(i).getLatLng().latitude);
            mapObjectInIteration.setLongitude(mapObjects.get(i).getLatLng().longitude);

            //Calc distance and add values that are in range to new list
            totalDistance = usersCurrentLocation.distanceTo(mapObjectInIteration);
            int totalDistanceInt = Math.round(totalDistance);

            if(totalDistanceInt <= userDistance){
                mapObjectsWithinLocation.add(mapObjects.get(i));
            }
        }
    }

    //Gets all cities and countries from quotes table and adds them to a custom made class object list
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

           try {
               double latitude = Double.parseDouble(latitudeString);
               double longitude = Double.parseDouble(longitudeString);
               MapObject mapObject = new MapObject(new LatLng(latitude, longitude), cityString, countryString,
               titleString, vendorString);
               mapObjects.add(mapObject);
           } catch (Exception e){
               e.printStackTrace();
           }
        }
    }
}
