package com.example.quoteme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ManageQuoteActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonDel;
    Button buttonEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_quote);

        buttonDel = findViewById(R.id.buttonDelete234);
        buttonEdit = findViewById(R.id.buttonEdit234);

        buttonDel.setOnClickListener(this);
        buttonDel.setOnClickListener(this);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_manageQuotes:
                Intent i = new Intent(this, ManageQuoteActivity.class);
                startActivity(i);
                return true;
            case R.id.action_signout:
                Intent ii = new Intent(this, LoginActivity.class);
                startActivity(ii);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == buttonDel){
            Toasty.success(this, getString(R.string.app_success), Toast.LENGTH_SHORT).show();
        } else if (v == buttonEdit) {
            Toasty.success(this, getString(R.string.app_success), Toast.LENGTH_SHORT).show();
        }
    }
}
