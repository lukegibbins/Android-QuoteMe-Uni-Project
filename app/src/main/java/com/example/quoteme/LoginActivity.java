package com.example.quoteme;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.QuoteData.QuoteDBhelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textSignUpNow;
    Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textSignUpNow = findViewById(R.id.textSignUpNow);
        textSignUpNow.setOnClickListener(this);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == textSignUpNow) {
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        }
         else if (v == buttonLogin){
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }
    }

    private void insertQuote() {

        ContentValues values = new ContentValues();

        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE, "Building Required");
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_DESCRIPTION, "I need a builder");
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_LOCATION, "Sunderland");
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_TELEPHONE, "07854919121");
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_STATUS, 0);
        values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR, "Builder");

        getContentResolver().insert(QuoteContract.QuoteEntry.CONTENT_URI, values);
    }

}
