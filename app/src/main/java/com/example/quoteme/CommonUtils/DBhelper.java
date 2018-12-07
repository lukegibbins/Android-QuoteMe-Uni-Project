package com.example.quoteme.CommonUtils;

import android.net.Uri;

public class DBhelper {

    public static final String DATABASE_NAME = "quoteme.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CONTENT_AUTHORITY = "com.example.quoteme";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
}
