package com.example.quoteme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;

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
    CheckBox chkTrade, chkArea;
    EditText editPostCode, editDistance;
    Spinner spinnerVendor;
    String latitude, longitude;
    private ArrayList<String> vendorList = new ArrayList<String>();

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

        buttonViewMap = findViewById(R.id.buttonMaps);
        buttonViewMap.setOnClickListener(this);
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings);
        buttonSaveSettings.setOnClickListener(this);

        vendorList.addAll(Arrays.asList(getResources().getStringArray(R.array.app_vendors)));

        spinnerVendor = findViewById(R.id.spinnerVendorsPrem);
        spinnerVendor.setEnabled(false);

        chkTrade = findViewById(R.id.checkAlertTrade);
        chkTrade.setOnCheckedChangeListener(this);

        editPostCode = findViewById(R.id.editPostCode);
        editDistance = findViewById(R.id.editDistance);

        chkArea = findViewById(R.id.checkAlertArea);
        chkArea.setOnCheckedChangeListener(this);

        boolean doesUserHavePreferences = createSharedPrefIfUserDoesNotExist();
        //if there is a file already here
        if(doesUserHavePreferences == true){
            //find out of notifications are enabled
            premiumPreferences = getSharedPreferences(usersEmail, MODE_PRIVATE);
            boolean vendorNotificationsEnabled = premiumPreferences.getBoolean("VENDOR_NOTIFICATIONS",false);
            boolean areaNotificationsEnabled = premiumPreferences.getBoolean("AREA_NOTIFICATIONS",false);

            //get notification data for vendor
            if(vendorNotificationsEnabled == true){
                int vendor_selection = premiumPreferences.getInt("VENDOR_TYPE",0);
                chkTrade.setChecked(true);
                spinnerVendor.setSelection(vendor_selection);
            }

            //get notification data for area
            if(areaNotificationsEnabled == true){
                int distanceKm = premiumPreferences.getInt("DIST",0);
                String postcode = premiumPreferences.getString("POSTCODE","Enter Postcode");
                editPostCode.setText(postcode);
                editDistance.setText(distanceKm);
                chkArea.setChecked(true);
            }
        } else{
            //set defaults
            premiumPreferences = getSharedPreferences(usersEmail, MODE_PRIVATE);
            SharedPreferences.Editor editor = premiumPreferences.edit();
            editor.putBoolean("VENDOR_NOTIFICATIONS", false);
            editor.putInt("VENDOR_TYPE", 0);
            editor.putInt("VENDOR_COUNT", 0);
            editor.putString("VENDOR_NAME", "null");

            editor.putBoolean("AREA_NOTIFICATIONS", false);
            editor.putInt("DIST", 0);
            editor.putString("POSTCODE", "no postcode set");
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
            if(chkTrade.isChecked() == true && spinnerVendor.getSelectedItemPosition() == 0){
                Toasty.info(this,"No selection has been made for vendor Notifications",
                        Toast.LENGTH_LONG).show();
            } else if (chkTrade.isChecked() && spinnerVendor.getSelectedItemPosition() > 0){
                SharedPreferences.Editor editor = premiumPreferences.edit();
                editor.putBoolean("VENDOR_NOTIFICATIONS", true);
                editor.putInt("VENDOR_TYPE", spinnerVendor.getSelectedItemPosition());
                String vendorString = vendorList.get(spinnerVendor.getSelectedItemPosition());
                int totalVendorCountInArea = getQuoteCountByVendor(vendorString);
                editor.putInt("VENDOR_COUNT", totalVendorCountInArea);
                editor.putString("VENDOR_NAME", vendorString);
                editor.apply();
                editor.commit();
                Toasty.success(this,"Preferences saved", Toast.LENGTH_LONG).show();
                finish();
            } else if (!chkTrade.isChecked()){
                SharedPreferences.Editor editor = premiumPreferences.edit();
                editor.putBoolean("VENDOR_NOTIFICATIONS", false);
                editor.putInt("VENDOR_TYPE", 0);
                editor.putInt("VENDOR_COUNT", 0);
                editor.putString("VENDOR_NAME", "null");
                editor.apply();
                editor.commit();
                Toasty.success(this,"Preferences saved",
                        Toast.LENGTH_LONG).show();
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
        if(chkTrade.isChecked()){
            spinnerVendor.setEnabled(true);
        } else if (!chkTrade.isChecked()){
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
}
