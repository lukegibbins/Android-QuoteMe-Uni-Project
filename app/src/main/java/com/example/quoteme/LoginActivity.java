package com.example.quoteme;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.QuoteData.QuoteDbHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textSignUpNow;
    Button buttonLogin;
    QuoteDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new QuoteDbHelper(this);

        textSignUpNow = findViewById(R.id.textSignUpNow);
        textSignUpNow.setOnClickListener(this);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);

        insertQuote();
    }

    @Override
    public void onClick(View v) {
        if(v == textSignUpNow) {
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        }
         else if (v == buttonLogin){
            Intent i = new Intent(this, HomeActivity.class);
            insertQuote();
            startActivity(i);
        }
    }

    private void insertQuote() {

        ContentValues values = new ContentValues();

        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE, "Building Required");
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION, "I need a builder");
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_IMAGE, "image.png");
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS, 1);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE, "07854919121");
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR, "Builder");
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION, "Sunderland");

        getContentResolver().insert(QuoteContract.QuoteEntry.CONTENT_URI, values);
    }

}
