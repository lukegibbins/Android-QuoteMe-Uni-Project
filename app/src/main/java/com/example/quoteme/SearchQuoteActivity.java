package com.example.quoteme;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.QuoteData.QuoteCursorAdapter;

import es.dmoral.toasty.Toasty;

import static com.example.quoteme.LoginActivity.FIRST_NAME;
import static com.example.quoteme.LoginActivity.PREMIUM;
import static com.example.quoteme.LoginActivity.SHARED_PREF_FILE;

public class SearchQuoteActivity extends AppCompatActivity implements View.OnClickListener {

    private QuoteCursorAdapter quoteCursorAdapter;
    EditText filteredLocation;
    Button buttonFilterSearch;
    ListView quoteListView;
    ImageView imageRefresh;
    TextView textInfo;

    private Spinner vendorSpinner;
    private String quoteVendorSpinner;
    private String premiumCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_quote);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        premiumCode = sharedPreferences.getString(PREMIUM, "0");

        FloatingActionButton fabSearch = findViewById(R.id.fabSearch);
        fabSearch.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(premiumCode.equals("1")){
                       Intent premiumIntent = new Intent(SearchQuoteActivity.this, PremiumAccessActivity.class);
                       startActivity(premiumIntent);
                   } else{
                       AlertDialog.Builder builder = new AlertDialog.Builder(SearchQuoteActivity.this);
                       builder.setTitle("No Premium Access")
                               .setMessage("Do you want to enable premium access?")
                               .setPositiveButton("Yes",
                                       new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int whichButton) {
                                               // do something...
                                           }
                                       })
                               .setNegativeButton("Cancel",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int whichButton) {
                                       dialog.dismiss();
                                   }
                               }
                       );
                       AlertDialog ad = builder.create();
                       ad.show();
                   }
               }
           });

        buttonFilterSearch = findViewById(R.id.buttonSearchQuote);
        buttonFilterSearch.setOnClickListener(this);
        filteredLocation = findViewById(R.id.editLocationSearch);

        imageRefresh = findViewById(R.id.imageRefresh);
        imageRefresh.setOnClickListener(this);

        //Defines view which holds data queried by cursorAdapter
        quoteListView = findViewById(R.id.search_list);
        textInfo = findViewById(R.id.textInfoHelp);

        if(quoteListView.getCount() < 1){
            textInfo.setVisibility(View.GONE);
        }

        //Used to display an empty view for a quoteListView that has 0 items
        View emptyView = findViewById(R.id.search_empty_title_text);
        quoteListView.setEmptyView(emptyView);

        //Vendor spinner
        vendorSpinner = findViewById(R.id.spinnerVendorsSearch);

        //Defines the items which should be displayed in quoteListView
        String [] project = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS
        };

        String selection = "status=?";
        String [] selectionArgs = {"0"};

        Cursor cursor = getContentResolver().query(QuoteContract.QuoteEntry.CONTENT_URI,
                project,
                selection,
                selectionArgs,
                null
        );

        //Create instance of cursorAdapter and bind cursorAdapter data to quoteList
        quoteCursorAdapter = new QuoteCursorAdapter(this, cursor);
        quoteListView.setAdapter(quoteCursorAdapter);

        quoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchQuoteActivity.this, RespondQuoteActivity.class);
                //When an item is clicked, it appends the ID of the item to the URI routing a specific quote
                Uri currentQuoteUri = ContentUris.withAppendedId(QuoteContract.QuoteEntry.CONTENT_URI, id);
                intent.setData(currentQuoteUri);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void filterSearchLocationVendor(){
        quoteVendorSpinner = vendorSpinner.getSelectedItem().toString().trim();
        String location = filteredLocation.getText().toString().trim();
        String selection = "location=? AND vendor=? AND status=?";
        String [] selectionArgs = {location, quoteVendorSpinner, "0"};

        String [] project = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS
        };

        Cursor cursor = getContentResolver().query(QuoteContract.QuoteEntry.CONTENT_URI,
                project,
                selection,
                selectionArgs,
                null
        );

        quoteCursorAdapter = new QuoteCursorAdapter(this, cursor);
        quoteListView.setAdapter(quoteCursorAdapter);
    }

    private void filterSearchLocation(){
        String location = filteredLocation.getText().toString().trim();
        String selection = "location=? AND status=?";
        String [] selectionArgs = {location, "0"};

        String [] project = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS

        };

        Cursor cursor = getContentResolver().query(QuoteContract.QuoteEntry.CONTENT_URI,
                project,
                selection,
                selectionArgs,
                null
        );

        quoteCursorAdapter = new QuoteCursorAdapter(this, cursor);
        quoteListView.setAdapter(quoteCursorAdapter);
    }

    private void filterSearchVendor(){
        quoteVendorSpinner = vendorSpinner.getSelectedItem().toString().trim();
        String selection = "vendor=? AND status=?";
        String [] selectionArgs = {quoteVendorSpinner, "0"};

        String [] project = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS
        };

        Cursor cursor = getContentResolver().query(QuoteContract.QuoteEntry.CONTENT_URI,
                project,
                selection,
                selectionArgs,
                null
        );

        quoteCursorAdapter = new QuoteCursorAdapter(this, cursor);
        quoteListView.setAdapter(quoteCursorAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_manageQuotes:
                Intent manageQuoteIntent = new Intent(this, ManageQuoteActivity.class);
                startActivity(manageQuoteIntent);
                return true;
            case R.id.action_signout:
                Intent signOutIntent = new Intent(this, LoginActivity.class);
                startActivity(signOutIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == buttonFilterSearch){
            String location = filteredLocation.getText().toString().trim();
            int vendorSpinnerPosition = vendorSpinner.getSelectedItemPosition();

                //if there are values for the location but the vendor is at element 0
                if(!location.isEmpty() && vendorSpinnerPosition == 0){
                filterSearchLocation();
            }
                //if there are values for the vendor but not location
                else if (location.isEmpty() && vendorSpinnerPosition > 0){
                filterSearchVendor();
            }
                //if there are values for both the vendor and location
                else if (!location.isEmpty() && vendorSpinnerPosition > 0){
                filterSearchLocationVendor();
            }
        } else if (v == imageRefresh){
            refreshList();
        }
    }

    private void refreshList(){
        //Defines the items which should be displayed in quoteListView
        String [] project = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS
        };

        String selection = "status=?";
        String [] selectionArgs = {"0"};

        Cursor cursor = getContentResolver().query(QuoteContract.QuoteEntry.CONTENT_URI,
                project,
                selection,
                selectionArgs,
                null
        );

        //Create instance of cursorAdapter and bind cursorAdapter data to quoteList
        quoteCursorAdapter = new QuoteCursorAdapter(this, cursor);
        quoteListView.setAdapter(quoteCursorAdapter);
    }
}
