package com.example.quoteme.QuoteData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import static com.example.quoteme.QuoteData.QuoteContract.QuoteEntry.TABLE_NAME;

public class QuoteProvider extends ContentProvider {

    private QuoteDbHelper quoteDbHelper;

    private static final int ALL_QUOTES = 100;
    private static final int SINGLE_QUOTE_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(QuoteContract.CONTENT_AUTHORITY, QuoteContract.PATH_QUOTES, ALL_QUOTES);
        sUriMatcher.addURI(QuoteContract.CONTENT_AUTHORITY,QuoteContract.PATH_QUOTES + "/#", SINGLE_QUOTE_ID);
    }

    @Override
    public boolean onCreate() {
        quoteDbHelper = new QuoteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @
            Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_QUOTES:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values){
        SQLiteDatabase database = quoteDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
