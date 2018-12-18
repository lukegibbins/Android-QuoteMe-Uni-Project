package com.example.quoteme;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.example.quoteme.LoginActivity.EMAIL;
import static com.example.quoteme.LoginActivity.SHARED_PREF_FILE;

public class RequestQuoteActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, SensorEventListener{

    //Sensors
    private SensorManager sensorManager;
    private long lastUpdate;

    private ArrayList<String> vendorList = new ArrayList<String>();
    private HashMap <String, Integer> vendorMappings = new HashMap<String, Integer>();
    private String quoteVendorSpinner;

    //0 for a status pending, 1 for a status accepted
    private static final int PENDING_QUOTE_STATUS = 0;
    private static final int EXISTING_QUOTE_LOADER = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    private Uri currentQuoteUri;
    private EditText quoteTitle, quoteLocationCity, quoteLocationCountry, quoteTel, quoteDescription;
    private Spinner vendorSpinner;

    ImageView imageCaptureCam;
    Button buttonSubmit, buttonImageUp, buttonDelete;

    double latitude;
    double longtitude;

    List<Address> geocodeMatches = null;

    String capturedImageFileName;
    String loadedImageFileName;
    Boolean hasPhotoBeenTaken = false;

    private String usersEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_quote);

        //sensor features
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        //add array list of vendors to list and populate hashMap
        vendorList.addAll(Arrays.asList(getResources().getStringArray(R.array.app_vendors)));
        populateHashMapWithVendors();

        //Gets incoming intent and possible Uri
        Intent receivingIntent = getIntent();
        currentQuoteUri = receivingIntent.getData();

        quoteTitle = findViewById(R.id.editTitle);
        quoteLocationCity = findViewById(R.id.editLocationCity);
        quoteLocationCountry = findViewById(R.id.editLocationCountry);

        //Permission
        ActivityCompat.requestPermissions(RequestQuoteActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        quoteTel = findViewById(R.id.editTel);
        quoteDescription = findViewById(R.id.editDesc);
        vendorSpinner = findViewById(R.id.spinnerVendorsSearch);

        buttonImageUp = findViewById(R.id.buttonImageUp);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonDelete = findViewById(R.id.buttonDeleteSpecific);

        imageCaptureCam = findViewById(R.id.imageCaptureCam);
        imageCaptureCam.setBackgroundResource(R.drawable.noimageselected);

        buttonSubmit.setOnClickListener(this);
        buttonImageUp.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        SharedPreferences sharedPreferences  = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        usersEmail = sharedPreferences.getString(EMAIL, "user email address");

        //Disable button if no camera
        if(!hasCamera()){
            buttonImageUp.setEnabled(false);
        }

        if(currentQuoteUri != null){
            setTitle(getString(R.string.app_editQuote));
            buttonSubmit.setText(getString(R.string.app_update));
            getSupportLoaderManager().initLoader(EXISTING_QUOTE_LOADER, null, this);
        } else if (currentQuoteUri == null){
            buttonDelete.setVisibility(View.GONE);
        }
    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void launchCameraAndSave(){
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String imageName = generatePictureName();
        capturedImageFileName = imageName;
        File imageFile = new File(pictureDir, imageName);
        Uri imageUri = Uri.fromFile(imageFile);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private String generatePictureName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = sdf.format(new Date());
        return "QUOTE_IMG_" + timeStamp + ".jpg";
    }

    private void saveQuote() {
        //Read data from field entries
        String quoteTitleString = quoteTitle.getText().toString().trim();
        String quoteLocationCityString = quoteLocationCity.getText().toString().trim();
        String quoteLocationCountryString = quoteLocationCountry.getText().toString().trim();
        String quoteTelString = quoteTel.getText().toString().trim();
        String quoteDescString = quoteDescription.getText().toString().trim();
        quoteVendorSpinner = vendorSpinner.getSelectedItem().toString().trim();
        String quoteImageString = capturedImageFileName;

        boolean valueChecker;
        if(quoteTitleString.isEmpty() ||
                quoteDescString.isEmpty() ||
                quoteLocationCityString.isEmpty() ||
                quoteLocationCountryString.isEmpty() ||
                quoteTelString.isEmpty()){
            valueChecker = false;
        } else {
            valueChecker = true;
        }

        //Fill DB table with values
        ContentValues values = new ContentValues();
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE, quoteTitleString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_CITY, quoteLocationCityString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_COUNTRY, quoteLocationCountryString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE, quoteTelString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION, quoteDescString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR, quoteVendorSpinner);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE, quoteImageString);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS, PENDING_QUOTE_STATUS);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_USER, usersEmail);

        //Determine if new or existing quote
        //If this is a new quote --> Do insert and validate
        if (currentQuoteUri == null) {
            if (valueChecker == true) {
                if (!quoteVendorSpinner.equals(vendorList.get(0))) {
                    try {
                        geocodeMatches = new Geocoder(this).getFromLocationName(
                                quoteLocationCityString+", "+quoteLocationCountryString, 1);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    if(!geocodeMatches.isEmpty()) {
                        latitude = geocodeMatches.get(0).getLatitude();
                        longtitude = geocodeMatches.get(0).getLongitude();
                        String latString = Double.toString(latitude);
                        String longString = Double.toString(longtitude);
                        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LATITUDE, latString);
                        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LONGITUDE, longString);
                    }
                    Uri newUri = getContentResolver().insert(QuoteContract.QuoteEntry.CONTENT_URI, values);
                    //Error saving quote
                    if (newUri == null) {
                        Toasty.error(this, "Error adding quote", Toast.LENGTH_LONG).show();
                        //Quote added successfully
                    } else {
                        Toasty.success(this, "Quote added. Quote status set to 'PENDING'", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, ManageQuoteActivity.class);
                        capturedImageFileName = null;
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
                if(quoteLocationCityString.isEmpty()){
                    quoteLocationCity.setError("This field can not be empty");
                }
                if(quoteLocationCountryString.isEmpty()){
                    quoteLocationCountry.setError("This field can not be empty");
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
                if(hasPhotoBeenTaken == false) {
                    values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE, loadedImageFileName);
                }
                try {
                    geocodeMatches = new Geocoder(this).getFromLocationName(
                            quoteLocationCityString+", "+quoteLocationCountryString, 1);
                }catch(IOException e){
                    e.printStackTrace();
                }
                if(!geocodeMatches.isEmpty()) {
                    latitude = geocodeMatches.get(0).getLatitude();
                    longtitude = geocodeMatches.get(0).getLongitude();
                    String latString = Double.toString(latitude);
                    String longString = Double.toString(longtitude);
                    values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LATITUDE, latString);
                    values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LONGITUDE, longString);
                }
                int rowsAffected = getContentResolver().update(currentQuoteUri, values, null, null);
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toasty.error(this, "Error updating quote", Toast.LENGTH_LONG).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toasty.success(this, "Quote updated.", Toast.LENGTH_LONG).show();
                    capturedImageFileName = null;
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
                if(quoteLocationCityString.isEmpty()){
                    quoteLocationCity.setError("This field can not be empty");
                }
                if(quoteLocationCountryString.isEmpty()){
                    quoteLocationCountry.setError("This field can not be empty");
                }
                if(quoteTelString.isEmpty()){
                    quoteTel.setError("This field can not be empty");
                }
            }
        }
    }

    //Delete a specific quote
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] project = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, project, null, null, null );
        if(cursor != null){
            if(cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow( project[0] );
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    private void launchGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSubmit) {
            saveQuote();
        } else if (v == buttonImageUp){
            //Every time this page is visited, ask for permission. We have to because of the API being 23
            //to access external storage

            AlertDialog.Builder builder = new AlertDialog.Builder(RequestQuoteActivity.this);
            builder.setTitle("Choose option")
                    .setItems(R.array.app_image_capture, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0){
                                launchCameraAndSave();
                            } else if(which == 1){
                                //MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                launchGallery();
                            } else {
                                //'CANCEL* action, do nothing
                            }
                        }
                    });
            AlertDialog ad = builder.create();
            ad.show();
        } else if (v == buttonDelete){
            deleteSpecificQuote();
        } else if (v == buttonImageUp){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                //Display saved image each time
                try {
                    hasPhotoBeenTaken = true;
                    String photoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            + "/" + capturedImageFileName;
                    galleryAddPic(photoPath);
                    Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                    imageCaptureCam.setBackgroundResource(0);
                    Bitmap rotateBitmap = rotateBitmap(bitmap, 90);
                    imageCaptureCam.setImageBitmap(rotateBitmap);
                } catch (Exception e){
                    Toast.makeText(this, "Unable to access storage", Toast.LENGTH_SHORT).show();
                }
            } else if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK){
                try {
                    hasPhotoBeenTaken = true;
                    Uri uri = data.getData();
                    String picturePath = getPath(this, uri);
                    String splitPathUsingSlashes[] = picturePath.split("/");
                    capturedImageFileName = splitPathUsingSlashes[5].trim();
                    String photoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            + "/" + capturedImageFileName;
                    Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                    Bitmap rotateBitmap = rotateBitmap(bitmap, 90);
                    imageCaptureCam.setBackgroundResource(0);
                    imageCaptureCam.setImageBitmap(rotateBitmap);

                } catch (Exception e) {
                    Toast.makeText(this, "Cant access image", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e){
            Toast.makeText(this, "Sorry", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                    Toast.makeText(this, "Permission denied to read your External storage",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
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
                QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_CITY,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_COUNTRY,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE
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
            int locationCityColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_CITY);
            int locationCountryColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION_COUNTRY);
            int telColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE);
            int descColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION);
            int vendorColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR);
            int imageColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String locationCity = cursor.getString(locationCityColumnIndex);
            String locationCountry = cursor.getString(locationCountryColumnIndex);
            String telephone = cursor.getString(telColumnIndex);
            String description = cursor.getString(descColumnIndex);
            String vendor = cursor.getString(vendorColumnIndex);

            // Update the views on the screen with the values from the database
            quoteTitle.setText(title);
            quoteLocationCity.setText(locationCity);
            quoteLocationCountry.setText(locationCountry);
            quoteTel.setText(telephone);
            quoteDescription.setText(description);
            vendorSpinner.setSelection(vendorMappings.get(vendor));

            //Try extract the imageName out of the row. If it is null, display default noImageSelected
            //If not null, then wipe the default image and replace with the image found in the db
            String imageTitle = cursor.getString(imageColumnIndex);

            //if there's no image and NO photo has been taken
            if(imageTitle == null && hasPhotoBeenTaken == false){
                imageCaptureCam.setBackgroundResource(R.drawable.noimageselected);
            }

            //db image is there, photo not taken and user has not pressed upload, they just back out
            else if (imageTitle != null && hasPhotoBeenTaken == false){
                String photoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/" + imageTitle;
                loadedImageFileName = imageTitle;
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                Bitmap rotateBitmap = rotateBitmap(bitmap, 90);
                imageCaptureCam.setBackgroundResource(0); //This works
                imageCaptureCam.setImageBitmap(rotateBitmap);
            }

            //if the user wants to replace a new image for a blank one
            else if(imageTitle == null && hasPhotoBeenTaken == true){
                imageCaptureCam.setBackgroundResource(0);
            }

            //if the user wants to replace an image for an new image
            else if (imageTitle != null && hasPhotoBeenTaken == true){
                String photoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/" + capturedImageFileName;
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                Bitmap rotateBitmap = rotateBitmap(bitmap, 90);
                imageCaptureCam.setBackgroundResource(0); //This works
                imageCaptureCam.setImageBitmap(rotateBitmap);
            }
        }
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelerationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelerationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            Toasty.info(this, "Values cleared from fields", Toast.LENGTH_SHORT).show();
            quoteTitle.setText("");
            quoteDescription.setText("");
            quoteLocationCity.setText("");
            quoteLocationCountry.setText("");
            quoteTel.setText("");
            vendorSpinner.setSelection(0);
        }
    }


    //Reset the fields to empty values after an edit in preparation for an insert
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        quoteTitle.setText("");
        quoteDescription.setText("");
        quoteLocationCity.setText("");
        quoteLocationCountry.setText("");
        quoteTel.setText("");
        vendorSpinner.setSelection(0);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
