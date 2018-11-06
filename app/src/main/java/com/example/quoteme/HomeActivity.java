package com.example.quoteme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    //OnCreate, add the menu_main.xml (menu with sub-menus) to the options bar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //On menu and submenu selection
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.action_background_red:
//                relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorRed));
//                return true;
//
//            case R.id.action_background_blue:
//                relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorBlue));
//                return true;
//
//            case R.id.action_background_default:
//                relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorDefault));
//                return true;
//
//            case R.id.action_user:
//                Intent intent = new Intent(this, Profile.class);
//                startActivity(intent);
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
