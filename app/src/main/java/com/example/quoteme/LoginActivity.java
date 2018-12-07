package com.example.quoteme;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
}
