package com.example.quoteme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PremiumAccessActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonViewMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_access);

        buttonViewMap = findViewById(R.id.buttonMaps);
        buttonViewMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonViewMap){
            Intent mapsIntent = new Intent(this ,MapsActivity.class);
            startActivity(mapsIntent);
        }
    }
}
