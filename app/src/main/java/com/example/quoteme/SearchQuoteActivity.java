package com.example.quoteme;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.QuoteData.QuoteCursorAdapter;

public class SearchQuoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private QuoteCursorAdapter quoteCursorAdapter;
    private static final int QUOTE_LOADER = 0;
    private TextView noQuotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_quote);

        noQuotes = findViewById(R.id.search_empty_title_text);

        //Defines view which holds data queried by cursorAdapter
        ListView quoteListView = findViewById(R.id.search_list);

        //Used to display an empty view for a quoteListView that has 0 items
        View emptyView = findViewById(R.id.search_empty_title_text);
        quoteListView.setEmptyView(emptyView);

        //Create instance of cursorAdapter and bind cursorAdapter data to quoteList
        quoteCursorAdapter = new QuoteCursorAdapter(this,null);
        quoteListView.setAdapter(quoteCursorAdapter);

        getSupportLoaderManager().initLoader(QUOTE_LOADER, null, this);
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
    protected void onStart() {
        super.onStart();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        //Defines the items which should be displayed in quoteListView
        String [] projection = {
                QuoteContract.QuoteEntry._ID,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE,
                QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR
        };

        return new CursorLoader(this,
                QuoteContract.QuoteEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        quoteCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        quoteCursorAdapter.swapCursor(null);
    }
}
