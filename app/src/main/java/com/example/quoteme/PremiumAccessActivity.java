package com.example.quoteme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quoteme.Models.MapObject;
import com.example.quoteme.QuoteData.QuoteContract;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.example.quoteme.LoginActivity.EMAIL;
import static com.example.quoteme.LoginActivity.SHARED_PREF_FILE;

public class PremiumAccessActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    Button buttonViewMap, buttonSaveSettings;
    CheckBox chkTradeArea;
    EditText editPostCode, editDistance;
    Spinner spinnerVendor;
    String latitude, longitude;

    private ArrayList<String> vendorList = new ArrayList<String>();
    private ArrayList<MapObject> mapObjects = new ArrayList<>();
    private ArrayList<MapObject> mapObjectsWithinLocation = new ArrayList<>();


    public final static String LAT_KEY = "LAT";
    public final static String LONG_KEY = "LONG";
    public final static String DISTANCE_KEY = "DIST";

    String usersEmail;
    SharedPreferences sharedPreferences;
    SharedPreferences premiumPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_access);
        vendorList.addAll(Arrays.asList(getResources().getStringArray(R.array.app_vendors)));

        buttonViewMap = findViewById(R.id.buttonMaps);
        buttonViewMap.setOnClickListener(this);
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings);
        buttonSaveSettings.setOnClickListener(this);

        spinnerVendor = findViewById(R.id.spinnerVendorsPrem);
        spinnerVendor.setEnabled(false);

        chkTradeArea = findViewById(R.id.checkAlertTrade);
        chkTradeArea.setOnCheckedChangeListener(this);

        editPostCode = findViewById(R.id.editPostCode);
        editDistance = findViewById(R.id.editDistance);

        //Checks to see if a user has a shared preferences file
        boolean doesUserHavePreferences = createSharedPrefIfUserDoesNotExist();
        //if so,
        if(doesUserHavePreferences == true){
            //find out of notifications are enabled
            premiumPreferences = getSharedPreferences(usersEmail, MODE_PRIVATE);
            boolean notificationsEnabled = premiumPreferences.getBoolean("NOTIFICATIONS",false);
            //get all notification preferences
            if(notificationsEnabled == true){
                int vendor_selection = premiumPreferences.getInt("VENDOR_TYPE",0);
                chkTradeArea.setChecked(true);
                spinnerVendor.setSelection(vendor_selection);
                int distanceKm = premiumPreferences.getInt("DIST",0);
                String postcode = premiumPreferences.getString("POSTCODE","Enter Postcode");
                if(postcode == "null") {
                    editPostCode.setText("");
                } else {
                    editPostCode.setText(postcode);
                }
                if(distanceKm == -1){
                    editPostCode.setText("");
                } else{
                    editDistance.setText(""+distanceKm);
                }
            }
        }
           //Notifications are not enabled
            else {
            //set defaults
            premiumPreferences = getSharedPreferences(usersEmail, MODE_PRIVATE);
            SharedPreferences.Editor editor = premiumPreferences.edit();
            editor.putBoolean("NOTIFICATIONS", false);
            editor.putInt("VENDOR_TYPE", 0);
            editor.putInt("VENDOR_COUNT", 0);
            editor.putString("VENDOR_NAME", "null");
            editor.putInt("DIST", -1);
            editor.putString("POSTCODE", "null");
            editor.putInt("QUOTE_AREA_COUNT", 0);
            editor.apply();
            editor.commit();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonViewMap) {
            if (editPostCode.getText().toString().equals("") || editDistance.getText().toString().equals("")) {
                editDistance.setError("Input data required");
                editPostCode.setError("Input data required");
            } else {
                    convertPostcodeToLatLng();
                    if (latitude != null || longitude != null) {
                        Intent mapsIntent = new Intent(this, MapsActivity.class);
                        mapsIntent.putExtra(LAT_KEY, latitude);
                        mapsIntent.putExtra(LONG_KEY, longitude);
                        int distanceInt = Integer.valueOf(editDistance.getText().toString());
                        mapsIntent.putExtra(DISTANCE_KEY, distanceInt);
                        startActivity(mapsIntent);
                    } else {
                        editPostCode.setError("Invalid postcode");
                    }
            }
        }

        if (v == buttonSaveSettings){
            //if the checkbox is checked but no vendor selection has been made
            if(chkTradeArea.isChecked() == true && spinnerVendor.getSelectedItemPosition() == 0){
                Toasty.info(this,"No selection has been made for vendor Notifications",
                        Toast.LENGTH_LONG).show();
            }
            //if checkbox is checked, vendor selection has been made
            else if (chkTradeArea.isChecked() && spinnerVendor.getSelectedItemPosition() > 0){
                //if editDistance and editPostcode both have a value
                if(editPostCode.getText().toString() != "" && editDistance.getText().toString() != ""){
                    //Check if the postcode is valid
                    convertPostcodeToLatLng();
                    if (latitude != null || longitude != null) {
                        SharedPreferences.Editor editor = premiumPreferences.edit();
                        editor.putBoolean("NOTIFICATIONS", true);
                        editor.putInt("VENDOR_TYPE", spinnerVendor.getSelectedItemPosition());
                        String vendorString = vendorList.get(spinnerVendor.getSelectedItemPosition());
                        int totalVendorCountInArea = getQuoteCountByVendor(vendorString);
                        editor.putInt("VENDOR_COUNT", totalVendorCountInArea);
                        editor.putString("VENDOR_NAME", vendorString);
                        editor.putString("POSTCODE", editPostCode.getText().toString());
                        String distString = editDistance.getText().toString();
                        try {
                            getAllData();
                            int dist = Integer.valueOf(distString);
                            filterMapDataByUsersLocation(dist, latitude, longitude);
                            int quoteAreaCount = mapObjectsWithinLocation.size();
                            editor.putInt("QUOTE_AREA_COUNT", quoteAreaCount);
                            editor.putInt("DIST", dist);
                            editor.apply();
                            editor.commit();
                            Toasty.info(this,"Setting updated", Toast.LENGTH_LONG).show();
                            finish();
                        } catch (Exception e){
                            editDistance.setError("Invalid distance");
                        }
                    } else {
                        editPostCode.setError("Invalid postcode");
                    }
                } else {
                    editPostCode.setError("This field is required to set notifications");
                    editDistance.setError("This field is required to set notifications");
                }
            }
               //If user chooses not to receive notifications anymore
                else if (!chkTradeArea.isChecked()){
                SharedPreferences.Editor editor = premiumPreferences.edit();
                editor.putBoolean("NOTIFICATIONS", false);
                editor.putInt("VENDOR_TYPE", 0);
                editor.putInt("VENDOR_COUNT", 0);
                editor.putInt("QUOTE_AREA_COUNT", 0);
                editor.putString("VENDOR_NAME", "null");
                editor.putString("POSTCODE", "null");
                editor.putInt("DIST", -1);
                editor.apply();
                editor.commit();
                Toasty.info(this,"Setting updated", Toast.LENGTH_LONG).show();
                finish();
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(chkTradeArea.isChecked()){
            spinnerVendor.setEnabled(true);
        } else if (!chkTradeArea.isChecked()){
            spinnerVendor.setEnabled(false);
        }
    }

    private void convertPostcodeToLatLng(){
        final Geocoder geocoder = new Geocoder(this);
        final String zip = editPostCode.getText().toString();
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

    private boolean createSharedPrefIfUserDoesNotExist(){
        Boolean fileExists = false;
        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        usersEmail = sharedPreferences.getString(EMAIL, "email");
        File file = new File("/data/data/com.example.quoteme/shared_prefs/" + usersEmail+".xml");
        if(file.exists()){
            fileExists = true;
        }
        return fileExists;
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

}
