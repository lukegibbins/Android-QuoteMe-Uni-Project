package com.example.quoteme.UserData;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.quoteme.CommonUtils.DBhelper.BASE_CONTENT_URI;
import static com.example.quoteme.CommonUtils.DBhelper.CONTENT_AUTHORITY;

public class UserContract {

    public static final String PATH_USERS = "users";

    //Constructor
    private UserContract(){ }

    public static final class UserEntry implements BaseColumns{
        //For cursor
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        //URI path
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERS);

        //Table Name
        public final static String TABLE_NAME_USERS = "users";

        //Table Columns
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_USERS_FIRSTNAME = "firstName";
        public final static String COLUMN_USERS_SURNAME = "surname";
        public static final String COLUMN_USERS_EMAIL = "email";
        public static final String COLUMN_USERS_PASSWORD = "password";
        public static final String COLUMN_USERS_PREMIUM = "premium";
    }
}
