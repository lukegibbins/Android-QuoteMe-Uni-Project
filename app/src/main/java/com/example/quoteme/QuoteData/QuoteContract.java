package com.example.quoteme.QuoteData;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.quoteme.CommonUtils.DBhelper.BASE_CONTENT_URI;
import static com.example.quoteme.CommonUtils.DBhelper.CONTENT_AUTHORITY;

public class QuoteContract  {

    public static final String PATH_QUOTES = "quotes";

    //Constructor
    private QuoteContract(){ }

    public static final class QuoteEntry implements BaseColumns{
        //For cursor
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUOTES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUOTES;

        //URI path
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_QUOTES);

        //Table Name
        public final static String TABLE_NAME = "quotes";

        //Table Columns
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_QUOTE_TITLE = "title";
        public final static String COLUMN_QUOTE_LOCATION = "location";
        public static final String COLUMN_QUOTE_TELEPHONE = "telephone";
        public static final String COLUMN_QUOTE_VENDOR = "vendor";
        public static final String COLUMN_QUOTE_DESCRIPTION = "description";
        public static final String COLUMN_QUOTE_IMAGE = "image";
        public static final String COLUMN_QUOTE_STATUS = "status";
        public static final String COLUMN_QUOTE_USER = "user";
    }
}
