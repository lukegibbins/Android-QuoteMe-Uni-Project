package com.example.quoteme;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quoteme.QuoteData.QuoteContract;

public class RespondQuoteActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentQuoteUri;
    private static final int EXISTING_QUOTE_LOADER = 0;
    private Button buttonAcceptQuote;
    private TextView quoteTitle, quoteLocation, quoteContact, quoteVendor, quoteDescription, quoteImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_quote);

        buttonAcceptQuote = findViewById(R.id.btnAcceptQuote);
        buttonAcceptQuote.setOnClickListener(this);

        quoteTitle = findViewById(R.id.textTitle);
        quoteLocation = findViewById(R.id.textLocation);
        quoteContact = findViewById(R.id.textContact);
        quoteVendor = findViewById(R.id.textVendor);
        quoteDescription = findViewById(R.id.textDescription);
        quoteImage = findViewById(R.id.textImage);


        //Gets incoming intent and possible Uri
        Intent receivingIntent = getIntent();
        currentQuoteUri = receivingIntent.getData();

        if (currentQuoteUri != null) {
            getSupportLoaderManager().initLoader(EXISTING_QUOTE_LOADER, null, this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,    // Parent activity context
                currentQuoteUri,                // Query the content URI for the current pet
                projection,                     // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection arguments
                null);                 // Default sort order
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
            //int imageColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String location = cursor.getString(locationColumnIndex);
            String telephone = cursor.getString(telColumnIndex);
            String description = cursor.getString(descColumnIndex);
            String vendor = cursor.getString(vendorColumnIndex);

            // Update the views on the screen with the values from the database
            quoteTitle.setText(title);
            quoteLocation.setText(location);
            quoteContact.setText(telephone);
            quoteDescription.setText(description);
            quoteVendor.setText(vendor);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        quoteTitle.setText("");
        quoteDescription.setText("");
        quoteLocation.setText("");
        quoteContact.setText("");
        quoteVendor.setText("");
    }

    @Override
    public void onClick(View v) {

    }
}
