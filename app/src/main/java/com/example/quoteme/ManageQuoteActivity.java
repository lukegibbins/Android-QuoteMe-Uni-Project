package com.example.quoteme;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.QuoteData.QuoteCursorAdapter;

import es.dmoral.toasty.Toasty;

public class ManageQuoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int QUOTE_LOADER = 0;
    private QuoteCursorAdapter quoteCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_quote);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageQuoteActivity.this);
                builder.setTitle(R.string.app_float_options_title)
                        .setItems(R.array.app_float_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    Intent launchAddQuoteIntent = new Intent(ManageQuoteActivity.this,
                                            RequestQuoteActivity.class);
                                    startActivity(launchAddQuoteIntent);
                                } else if(which == 1){
                                    new AlertDialog.Builder(ManageQuoteActivity.this)
                                            .setMessage(getString(R.string.app_deleteAllConfirmation))
                                            .setCancelable(false)
                                            .setPositiveButton(getString(R.string.app_yes), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    deleteAllQuotes();
                                                }
                                            })
                                            .setNegativeButton(getString(R.string.app_no), null).show();
                                } else {
                                    //'CANCEL* action, do nothing
                                }
                            }
                        });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });

        //Defines view which holds data queried by cursorAdapter
        ListView quoteListView = findViewById(R.id.list);

        //Used to display an empty view for a quoteListView that has 0 items
        View emptyView = findViewById(R.id.empty_view);
        quoteListView.setEmptyView(emptyView);

        //Create instance of cursorAdapter and bind cursorAdapter data to quoteList
        quoteCursorAdapter = new QuoteCursorAdapter(this,null);
        quoteListView.setAdapter(quoteCursorAdapter);


        quoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManageQuoteActivity.this, RequestQuoteActivity.class);
                //When an item is clicked, it appends the ID of the item to the URI routing a specific quote
                Uri currentQuoteUri = ContentUris.withAppendedId(QuoteContract.QuoteEntry.CONTENT_URI, id);
                intent.setData(currentQuoteUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(QUOTE_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void deleteAllQuotes(){
        int deletedCount = getContentResolver().delete(QuoteContract.QuoteEntry.CONTENT_URI,null,null);
        Toasty.info(this, deletedCount + " "+getString(R.string.app_deleteQuotesInfo), Toast.LENGTH_LONG).show();
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
