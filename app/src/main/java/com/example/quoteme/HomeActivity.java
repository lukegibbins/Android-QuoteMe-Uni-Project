package com.example.quoteme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quoteme.Models.MapObject;
import com.example.quoteme.QuoteData.QuoteContract;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.quoteme.LoginActivity.EMAIL;
import static com.example.quoteme.LoginActivity.FIRST_NAME;
import static com.example.quoteme.LoginActivity.SHARED_PREF_FILE;
import static com.example.quoteme.LoginActivity.SURNAME;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button requestWork, searchWork;
    ImageView imageView;
    TextView textViewUsername;

    private String firstName;
    private String surname;

    String latitude, longitude;

    SharedPreferences premiumPreferences, sharedPreferences;
    private ArrayList<MapObject> mapObjects = new ArrayList<>();
    private ArrayList<MapObject> mapObjectsWithinLocation = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(R.color.primaryTextColor));

        requestWork = findViewById(R.id.buttonRequestWork);
        requestWork.setOnClickListener(this);

        searchWork = findViewById(R.id.buttonSearchWork);
        searchWork.setOnClickListener(this);

        SharedPreferences sharedPreferences  = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        firstName = sharedPreferences.getString(FIRST_NAME, "first name");
        surname = sharedPreferences.getString(SURNAME, "surname");

        textViewUsername = findViewById(R.id.textViewUserName);
        textViewUsername.setText(firstName + " " + surname);

        imageView = findViewById(R.id.imageViewConstr);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == requestWork){
            Intent i  = new Intent(this, RequestQuoteActivity.class);
            startActivity(i);
        } else if (v == searchWork){
            Intent i  = new Intent(this, SearchQuoteActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_manageQuotes:
                Intent manageQuotesIntent = new Intent(this, ManageQuoteActivity.class);
                startActivity(manageQuotesIntent);
                return true;
            case R.id.action_signout:
                Intent signOutIntent = new Intent(this, LoginActivity.class);
                startActivity(signOutIntent);
            case R.id.action_notification:
                displayVendorNotificationsIfEnabled();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void convertPostcodeToLatLng(String zipCode){
        final Geocoder geocoder = new Geocoder(this);
        final String zip = zipCode;
        try {
            List<Address> addresses = geocoder.getFromLocationName(zip, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Use the address as needed
                latitude = String.valueOf(address.getLatitude());
                longitude = String.valueOf(address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     public void displayVendorNotificationsIfEnabled(){
        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        String usersEmail = sharedPreferences.getString(EMAIL,"email");
        premiumPreferences = getSharedPreferences(usersEmail, MODE_PRIVATE);
        boolean notificationsEnabled = premiumPreferences.getBoolean("NOTIFICATIONS",false);

        if(notificationsEnabled == true){
         View layout = findViewById(android.R.id.content);
         String vendorName = premiumPreferences.getString("VENDOR_NAME","null");
         String postcode = premiumPreferences.getString("POSTCODE","your postcode");
         int distanceKm = premiumPreferences.getInt("DIST",0);
         int vendorCount = getQuoteCountByVendor(vendorName);

         getAllData();
         convertPostcodeToLatLng(postcode);
         filterMapDataByUsersLocation(distanceKm,latitude, longitude);

         Snackbar snackbar = Snackbar.make(layout, "There is "+ vendorCount +
                 " quote(s) available for an "+ vendorName+", and " + mapObjectsWithinLocation.size() + " quotes in total within "+ distanceKm
                         +"Km of "+postcode+".", + Snackbar.LENGTH_INDEFINITE)
                 .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //dismiss
                    }
                });
            snackbar.show();
        }


        else {
            View layout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(layout, "Notifications are not currently enabled. " +
                            "You can enable notifications in 'Search Work'",
                    + Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //dismiss
                        }
                    });
            snackbar.show();
        }
    }


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


    private void filterMapDataByUsersLocation(int usersDistanceKm, String latFromPostcode, String longFromPostcode){
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

        //Users set parameter distance. Convert Km to M
        int userDistance = usersDistanceKm * 1000;

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

    private int getQuoteCountByVendor(String vendorString) {
        String selection = "vendor=? AND status=?";
        String [] selectionArgs = {vendorString, "0"};

        String [] project = {
                QuoteContract.QuoteEntry._ID,

        };

        Cursor cursor = getContentResolver().query(QuoteContract.QuoteEntry.CONTENT_URI,
                project,
                selection,
                selectionArgs,
                null
        );
        return cursor.getCount();
    }


}