package com.example.quoteme;

import android.content.Intent;
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

import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PremiumAccessActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    Button buttonViewMap;
    CheckBox chkTrade, chkArea;
    EditText editPostCode;
    Spinner spinnerVendor;
    String latitude, longitude;

    public final static String LAT_KEY = "LAT";
    public final static String LONG_KEY = "LONG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_access);

        buttonViewMap = findViewById(R.id.buttonMaps);
        buttonViewMap.setOnClickListener(this);

        spinnerVendor = findViewById(R.id.spinnerVendorsPrem);
        spinnerVendor.setEnabled(false);

        chkTrade = findViewById(R.id.checkAlertTrade);
        chkTrade.setOnCheckedChangeListener(this);

        editPostCode = findViewById(R.id.editPostCode);

        chkArea = findViewById(R.id.checkAlertArea);
        chkArea.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonViewMap) {
            if (!editPostCode.getText().toString().equals("")) {
                convertPostcodeToLatLng();
                if (latitude == null || longitude == null) {
                    Toasty.error(this, "Invalid postcode", Toast.LENGTH_SHORT).show();
                } else {
                    Intent mapsIntent = new Intent(this, MapsActivity.class);
                    mapsIntent.putExtra(LAT_KEY, latitude);
                    mapsIntent.putExtra(LONG_KEY, longitude);
                    startActivity(mapsIntent);
                }
            } else{
                Toasty.error(this, "Your postcode is required to set your location",
                        Toast.LENGTH_LONG).show();
            }
        }
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
                Toasty.info(this, "Location received", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Enter a correct postcode", Toast.LENGTH_LONG).show();
        }
    }
}
