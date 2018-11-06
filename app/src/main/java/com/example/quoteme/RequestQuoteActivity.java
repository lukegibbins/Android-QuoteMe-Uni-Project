package com.example.quoteme;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class RequestQuoteActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_quote);


        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSubmit) {
            Toasty.info(this, getString(R.string.app_success), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
