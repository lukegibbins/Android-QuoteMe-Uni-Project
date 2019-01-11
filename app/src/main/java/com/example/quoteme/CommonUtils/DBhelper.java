package com.example.quoteme.CommonUtils;

import android.net.Uri;

//This is a class separated from the quote and user data as it can be used
//on any types of data. The data listed below are the for both types of data
public class DBhelper {

    //Database name and version
    public static final String DATABASE_NAME = "quoteme.db";
    public static final int DATABASE_VERSION = 1;

    //Base URI information
    public static final String CONTENT_AUTHORITY = "com.example.quoteme";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
}
