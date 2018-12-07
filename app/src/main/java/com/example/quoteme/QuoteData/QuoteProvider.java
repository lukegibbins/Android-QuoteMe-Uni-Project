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
import com.example.quoteme.UserData.UserContract;
import static com.example.quoteme.CommonUtils.DBhelper.CONTENT_AUTHORITY;
import static com.example.quoteme.QuoteData.QuoteContract.QuoteEntry.TABLE_NAME_QUOTE;
import static com.example.quoteme.UserData.UserContract.UserEntry.TABLE_NAME_USERS;

public class QuoteProvider extends ContentProvider {

    private static final int ALL_QUOTES = 100;
    private static final int SINGLE_QUOTE_ID = 101;
    private static final int ALL_USERS = 200;
    private static final int SINGLE_USER_ID = 201;

    private QuoteDbHelper quoteDbHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, QuoteContract.PATH_QUOTES, ALL_QUOTES);
        sUriMatcher.addURI(CONTENT_AUTHORITY,QuoteContract.PATH_QUOTES + "/#", SINGLE_QUOTE_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, UserContract.PATH_USERS, ALL_USERS);
        sUriMatcher.addURI(CONTENT_AUTHORITY,UserContract.PATH_USERS + "/#", SINGLE_USER_ID);
    }

    @Override
    public boolean onCreate() {
        quoteDbHelper = new QuoteDbHelper(getContext());
        return true;
    }

    //Used to actually fill in the data in the quoteViewList
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = quoteDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            //Cursor could contain multiple rows of data
            case ALL_QUOTES:
                cursor = database.query(QuoteContract.QuoteEntry.TABLE_NAME_QUOTE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            //Cursor contain 1 specific row using an ID. E.g "content://com.example.android.quotes/quotes/3",
            case SINGLE_QUOTE_ID:
                selection = QuoteContract.QuoteEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(QuoteContract.QuoteEntry.TABLE_NAME_QUOTE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case ALL_USERS:
                cursor = database.query(TABLE_NAME_USERS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case SINGLE_USER_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABLE_NAME_USERS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor, so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_QUOTES:
                return QuoteContract.QuoteEntry.CONTENT_LIST_TYPE;
            case SINGLE_QUOTE_ID:
                return QuoteContract.QuoteEntry.CONTENT_ITEM_TYPE;
            case ALL_USERS:
                return UserContract.UserEntry.CONTENT_LIST_TYPE;
            case SINGLE_USER_ID:
                return UserContract.UserEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_QUOTES:
                return insertQuote(uri, values);
            case ALL_USERS:
                return insertUser(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertQuote(Uri uri, ContentValues values){
        SQLiteDatabase database = quoteDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME_QUOTE, null, values);
        if (id == -1) {
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertUser(Uri uri, ContentValues values){
        SQLiteDatabase database = quoteDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME_USERS, null, values);
        if (id == -1) {
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get database instance to write to
        SQLiteDatabase database = quoteDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_QUOTES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(QuoteContract.QuoteEntry.TABLE_NAME_QUOTE, selection, selectionArgs);
                break;
            case SINGLE_QUOTE_ID:
                // Delete a single row given by the ID in the URI
                selection = QuoteContract.QuoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(QuoteContract.QuoteEntry.TABLE_NAME_QUOTE, selection, selectionArgs);
                break;
            case ALL_USERS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(UserContract.UserEntry.TABLE_NAME_USERS, selection, selectionArgs);
                break;
            case SINGLE_USER_ID:
                // Delete a single row given by the ID in the URI
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(UserContract.UserEntry.TABLE_NAME_USERS, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_QUOTES:
                return updateQuote(uri, values, selection, selectionArgs);
            case SINGLE_QUOTE_ID:
                selection = QuoteContract.QuoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateQuote(uri, values, selection, selectionArgs);
            case ALL_USERS:
                return updateUser(uri, values, selection, selectionArgs);
            case SINGLE_USER_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateUser(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateQuote(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = quoteDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(QuoteContract.QuoteEntry.TABLE_NAME_QUOTE, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateUser(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = quoteDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(UserContract.UserEntry.TABLE_NAME_USERS, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
