package com.example.quoteme;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RequestQuoteActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayList<String> vendorList = new ArrayList<String>();
    private HashMap <String, Integer> vendorMappings = new HashMap<String, Integer>();

    //0 for a status pending, 1 for a status accepted
    private static final int PENDING_QUOTE_STATUS = 0;
    private static final int EXISING_QUOTE_LOADER = 0;

    private Uri currentQuoteUri;
    private EditText quoteTitle, quoteLocation, quoteTel, quoteDescription;
    private Spinner vendorSpinner;

    Button buttonSubmit;
    Button buttonImageUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_quote);

        //gets stringArray vendor values and pops them into a HashMap
        populateHashMapWithVendors();

        //Gets incoming intent and possible Uri
        Intent receivingIntent = getIntent();
        currentQuoteUri = receivingIntent.getData();


        quoteTitle = findViewById(R.id.editTitle);
        quoteLocation = findViewById(R.id.editLocation);
        quoteTel = findViewById(R.id.editTel);
        quoteDescription = findViewById(R.id.editDesc);
        vendorSpinner = findViewById(R.id.spinnerVendors);

        buttonImageUp = findViewById(R.id.buttonImageUp);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(this);
        buttonImageUp.setOnClickListener(this);

        if(currentQuoteUri != null){
            setTitle(getString(R.string.app_editQuote));
            buttonSubmit.setText(getString(R.string.app_update));
            getSupportLoaderManager().initLoader(EXISING_QUOTE_LOADER, null, this);
        }
    }

    private void saveQuote(){
        //Read data from field entries
        String quoteTitleString = quoteTitle.getText().toString().trim();
        String quoteLocationString = quoteLocation.getText().toString().trim();
        String quoteTelString = quoteTel.getText().toString().trim();
        String quoteDescString = quoteDescription.getText().toString().trim();
        String quoteVendorSpinner = vendorSpinner.getSelectedItem().toString().trim();
        String quoteImageString = "quote.png"; //<-- changed when figure out

        //Fill DB table with values
        ContentValues values = new ContentValues();
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE, quoteTitleString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION, quoteLocationString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE, quoteTelString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION, quoteDescString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR, quoteVendorSpinner);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE, quoteImageString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS, PENDING_QUOTE_STATUS);

    }

    private void populateHashMapWithVendors(){
        vendorList.addAll(Arrays.asList(getResources().getStringArray(R.array.app_vendors)));

        for(int i = 1; i < vendorList.size(); i++){
            vendorMappings.put(vendorList.get(i), i);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSubmit) {
            Toasty.success(this, getString(R.string.app_success), Toast.LENGTH_SHORT).show();
        }
        else if (v == buttonImageUp){
            Toasty.success(this, getString(R.string.app_success), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_manageQuotes:
                Intent i = new Intent(this, ManageQuoteActivity.class);
                startActivity(i);
                return true;
            case R.id.action_signout:
                Intent ii = new Intent(this, LoginActivity.class);
                startActivity(ii);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
