package com.example.quoteme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

public class PremiumAccessActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    Button buttonViewMap;
    CheckBox chkTrade;
    Spinner spinnerVendor;

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
    }

    @Override
    public void onClick(View v) {
        if(v == buttonViewMap){
            Intent mapsIntent = new Intent(this ,MapsActivity.class);
            startActivity(mapsIntent);
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
}
