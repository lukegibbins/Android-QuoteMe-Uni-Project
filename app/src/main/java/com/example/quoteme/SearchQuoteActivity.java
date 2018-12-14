package com.example.quoteme;

import android.content.ContentUris;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.QuoteData.QuoteCursorAdapter;
import com.example.quoteme.UserData.UserContract;

public class SearchQuoteActivity extends AppCompatActivity implements View.OnClickListener {

    private QuoteCursorAdapter quoteCursorAdapter;
    EditText filteredLocation;
    Button buttonFilterSearch;
    ListView quoteListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_quote);

        buttonFilterSearch = findViewById(R.id.buttonSearchQuote);
        buttonFilterSearch.setOnClickListener(this);

        filteredLocation = findViewById(R.id.editLocationSearch);

        //Defines view which holds data queried by cursorAdapter
        quoteListView = findViewById(R.id.search_list);

        //Used to display an empty view for a quoteListView that has 0 items
        View emptyView = findViewById(R.id.search_empty_title_text);
        quoteListView.setEmptyView(emptyView);

        //Defines the items which should be displayed in quoteListView
        String [] project = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR
        };

        Cursor cursor = getContentResolver().query(QuoteContract.QuoteEntry.CONTENT_URI,
                project,
                null,
                null,
                null
        );

        //Create instance of cursorAdapter and bind cursorAdapter data to quoteList
        quoteCursorAdapter = new QuoteCursorAdapter(this, cursor);
        quoteListView.setAdapter(quoteCursorAdapter);

        quoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchQuoteActivity.this, RequestQuoteActivity.class);
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

    private void filterSearch(){
        String location = filteredLocation.getText().toString().trim();
        String selection = "location=?";
        String [] selectionArgs = {location};

        String [] project = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR
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
            //filterSearch();
            Intent i = new Intent(this, RespondQuoteActivity.class);
            startActivity(i);
        }
    }
}
