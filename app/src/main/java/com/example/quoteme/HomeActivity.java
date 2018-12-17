package com.example.quoteme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.quoteme.LoginActivity.FIRST_NAME;
import static com.example.quoteme.LoginActivity.SHARED_PREF_FILE;
import static com.example.quoteme.LoginActivity.SURNAME;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button requestWork, searchWork;
    ImageView imageView;
    TextView textViewUsername;

    private String firstName;
    private String surname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(R.color.primaryTextColor));

        requestWork = findViewById(R.id.buttonRequestWork);
        requestWork.setOnClickListener(this);

        searchWork = findViewById(R.id.buttonSearchWork);
        searchWork.setOnClickListener(this);

        SharedPreferences sharedPreferences  = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        firstName = sharedPreferences.getString(FIRST_NAME, "first name");
        surname = sharedPreferences.getString(SURNAME, "surname");

        textViewUsername = findViewById(R.id.textViewUserName);
        textViewUsername.setText(firstName + " " + surname);

        imageView = findViewById(R.id.imageViewConstr);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == requestWork){
            Intent i  = new Intent(this, RequestQuoteActivity.class);
            startActivity(i);
        } else if (v == searchWork){
            Intent i  = new Intent(this, SearchQuoteActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_manageQuotes:
                Intent manageQuotesIntent = new Intent(this, ManageQuoteActivity.class);
                startActivity(manageQuotesIntent);
                return true;
            case R.id.action_signout:
                Intent signOutIntent = new Intent(this, LoginActivity.class);
                startActivity(signOutIntent);
            case R.id.action_premium:
                Intent premiumAccessIntent = new Intent(this, PremiumAccessActivity.class);
                startActivity(premiumAccessIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
