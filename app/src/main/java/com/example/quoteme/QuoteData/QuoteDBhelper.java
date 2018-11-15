package com.example.quoteme.QuoteData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.quoteme.CommonUtils.DBhelper.DATABASE_NAME;
import static com.example.quoteme.CommonUtils.DBhelper.DATABASE_VERSION;

public class QuoteDBhelper extends SQLiteOpenHelper {

    //Constructor
    public QuoteDBhelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_QUOTE_TABLE =  "CREATE TABLE " + QuoteContract.QuoteEntry.TABLE_NAME + " ("
                + QuoteContract.QuoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE + " TEXT NOT NULL, "
                + QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION + " TEXT NOT NULL, "
                + QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE + " TEXT, "
                + QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                + QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE + " INTEGER NOT NULL, "
                + QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR + " TEXT NOT NULL, "
                + QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_QUOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Increase when versionNo changes
    }
}
