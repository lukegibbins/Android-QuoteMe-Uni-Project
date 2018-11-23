package com.example.quoteme;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class RequestQuoteActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayList<String> vendorList = new ArrayList<String>();
    private HashMap <String, Integer> vendorMappings = new HashMap<String, Integer>();
    private String quoteVendorSpinner;

    //0 for a status pending, 1 for a status accepted
    private static final int PENDING_QUOTE_STATUS = 0;
    private static final int EXISTING_QUOTE_LOADER = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
    private String pictureName;

    private Uri currentQuoteUri;
    private EditText quoteTitle, quoteLocation, quoteTel, quoteDescription;
    private Spinner vendorSpinner;

    ImageView imageCaptureCam;
    Button buttonSubmit, buttonImageUp, buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_quote);

        //add array list of vendors to list and populate hashMap
        vendorList.addAll(Arrays.asList(getResources().getStringArray(R.array.app_vendors)));
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
        buttonDelete = findViewById(R.id.buttonDeleteSpecific);

        imageCaptureCam = (ImageView) findViewById(R.id.imageCaptureCam);

        //Disable button if no camera
        if(!hasCamera()){
            buttonImageUp.setEnabled(false);
        }

        buttonSubmit.setOnClickListener(this);
        buttonImageUp.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        if(currentQuoteUri != null){
            setTitle(getString(R.string.app_editQuote));
            buttonSubmit.setText(getString(R.string.app_update));
            getSupportLoaderManager().initLoader(EXISTING_QUOTE_LOADER, null, this);
        } else if (currentQuoteUri == null){
            buttonDelete.setVisibility(View.GONE);
        }
    }

    //Checks if app has camera
    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void launchCamera(){

        //Launch camera and save image to external storage >> storage/emulator/0/
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Save to SD CARD. Get directory to save to, generate a pic name and add to SD
        //Gets location (File explorer >> SD card >> pictures
        File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //generate unique pic name using method
        pictureName = generatePictureName();
        addSharedPreferences(pictureName);
        //Coverts to image file and concatenates dir and image name
        File imageFile = new File(pictureDir, pictureName);
        //coverts to URi
        Uri pictureUri = Uri.fromFile(imageFile);
        //Adds image to location
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void addSharedPreferences(String pictureName){
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //Key and Value
        editor.putString(pictureName, pictureName);
    }


    private String generatePictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = sdf.format(new Date());
        return "QUOTE_IMG_" + timeStamp + ".jpg";
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
                String s = sp.getString(pictureName, "No Image");
                System.out.println("*****************hey**" + pictureName);
            }
        } catch (Exception e){
            Toast.makeText(this, "Sorry", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveQuote() {
        //Read data from field entries
        String quoteTitleString = quoteTitle.getText().toString().trim();
        String quoteLocationString = quoteLocation.getText().toString().trim();
        String quoteTelString = quoteTel.getText().toString().trim();
        String quoteDescString = quoteDescription.getText().toString().trim();
        quoteVendorSpinner = vendorSpinner.getSelectedItem().toString().trim();
        String quoteImageString = "quote.png"; //<-- changed when figured out

        boolean valueChecker;
        if(quoteTitleString.isEmpty() ||
                quoteDescString.isEmpty() ||
                quoteLocationString.isEmpty() ||
                quoteTelString.isEmpty()){
            valueChecker = false;
        } else {
            valueChecker = true;
        }

        //Fill DB table with values
        ContentValues values = new ContentValues();
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE, quoteTitleString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION, quoteLocationString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE, quoteTelString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION, quoteDescString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR, quoteVendorSpinner);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE, quoteImageString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS, PENDING_QUOTE_STATUS);

        //Determine if new or existing quote
        //If this is a new quote --> Do insert and validate
        if (currentQuoteUri == null) {
            if (valueChecker == true) {
                if (!quoteVendorSpinner.equals(vendorList.get(0))) {
                    Uri newUri = getContentResolver().insert(QuoteContract.QuoteEntry.CONTENT_URI, values);
                    //Error saving quote
                    if (newUri == null) {
                        Toasty.error(this, "Error adding quote", Toast.LENGTH_LONG).show();
                        //Quote added successfully
                    } else {
                        Toasty.success(this, "Quote added. Quote status set to 'pending'", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, ManageQuoteActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toasty.error(this, "Please select a vendor from the list", Toast.LENGTH_LONG).show();
                }
            } else{
                if(quoteDescString.isEmpty()){
                    quoteDescription.setError("This field can not be empty");
                }
                if(quoteTitleString.isEmpty()){
                    quoteTitle.setError("This field can not be empty");
                }
                if(quoteLocationString.isEmpty()){
                    quoteLocation.setError("This field can not be empty");
                }
                if(quoteTelString.isEmpty()){
                    quoteTel.setError("This field can not be empty");
                }
            }
        }
        //Else, do update and validate
        else {
            if (valueChecker == true) {
                if (!quoteVendorSpinner.equals(vendorList.get(0))){
                //We want to update the current row as the data being passed through has a Uri
                int rowsAffected = getContentResolver().update(currentQuoteUri, values, null, null);
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toasty.error(this, "Error updating quote", Toast.LENGTH_LONG).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toasty.success(this, "Quote updated.", Toast.LENGTH_LONG).show();
                    finish();

                }
            } else {
                    Toasty.error(this, "Please select a vendor from the list", Toast.LENGTH_LONG).show();
                }
         } else {
                if(quoteDescString.isEmpty()){
                    quoteDescription.setError("This field can not be empty");
                }
                if(quoteTitleString.isEmpty()){
                    quoteTitle.setError("This field can not be empty");
                }
                if(quoteLocationString.isEmpty()){
                    quoteLocation.setError("This field can not be empty");
                }
                if(quoteTelString.isEmpty()){
                    quoteTel.setError("This field can not be empty");
                }
            }
        }
    }

    private void deleteSpecificQuote(){
        int deletedCount = getContentResolver().delete(currentQuoteUri, null, null);
        Toasty.info(this, deletedCount + " "+getString(R.string.app_deleteQuotesInfo), Toast.LENGTH_LONG).show();
        finish();
    }

    //Sets up a key-value pair list with list items and values but disregards first element
    private void populateHashMapWithVendors(){
        for(int i = 1; i < vendorList.size(); i++){
            vendorMappings.put(vendorList.get(i), i);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == buttonSubmit) {
            saveQuote();
        } else if (v == buttonImageUp){
            launchCamera();
        } else if (v == buttonDelete){
            deleteSpecificQuote();
        } else if (v == buttonImageUp){

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
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                currentQuoteUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //If the cursor is null or there is no items in the list, back out
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE);
            int locationColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION);
            int telColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE);
            int descColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION);
            int vendorColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String location = cursor.getString(locationColumnIndex);
            String telephone = cursor.getString(telColumnIndex);
            String description = cursor.getString(descColumnIndex);
            String vendor = cursor.getString(vendorColumnIndex);

            // Update the views on the screen with the values from the database
            quoteTitle.setText(title);
            quoteLocation.setText(location);
            quoteTel.setText(telephone);
            quoteDescription.setText(description);
            vendorSpinner.setSelection(vendorMappings.get(vendor));
        }
    }


    //Reset the fields to empty values after an edit in preparation for an insert
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        quoteTitle.setText("");
        quoteDescription.setText("");
        quoteLocation.setText("");
        quoteTel.setText("");
        vendorSpinner.setSelection(0);
    }
}
