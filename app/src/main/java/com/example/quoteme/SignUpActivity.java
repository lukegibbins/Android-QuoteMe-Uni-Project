package com.example.quoteme;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.UserData.UserContract;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener,
        GestureDetector.OnGestureListener {

    Button buttonSubmit;
    private EditText userFirstName, userSurname, userEmail, userPassword, userConfirmPassword;
    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        buttonSubmit = findViewById(R.id.buttonSignUp);
        buttonSubmit.setOnClickListener(this);

        userFirstName = findViewById(R.id.editFirstName);
        userSurname = findViewById(R.id.editSurname);
        userEmail = findViewById(R.id.editEmail);
        userPassword = findViewById(R.id.editPasswordSignup);
        userConfirmPassword = findViewById(R.id.editConfirmPassword);

        gestureDetector = new GestureDetectorCompat(this,this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSubmit){
            addUser();
        }
    }

    private void addUser(){
        //Fill DB table with values

        String userFirstNameString = userFirstName.getText().toString().trim();
        String userSurnameString = userSurname.getText().toString().trim();
        String userEmailString = userEmail.getText().toString().trim();
        String userPasswordString = userPassword.getText().toString().trim();
        String userPasswordConfirmString = userConfirmPassword.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_USERS_FIRSTNAME, userFirstNameString);
        values.put(UserContract.UserEntry.COLUMN_USERS_SURNAME, userSurnameString);
        values.put(UserContract.UserEntry.COLUMN_USERS_EMAIL, userEmailString);
        values.put(UserContract.UserEntry.COLUMN_USERS_PASSWORD, userPasswordString);

        if(userPasswordString == userPasswordConfirmString){
            Uri newUri = getContentResolver().insert(UserContract.UserEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toasty.error(this, "Error creating account", Toast.LENGTH_LONG).show();
            } else {
                Toasty.success(this, "Successfully registered", Toast.LENGTH_LONG).show();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
            }
        } else {
            Toasty.error(this, "Password do not match", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e2.getX() - e1.getX() > 50) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            return true;
        } else
            return false;
    }
}
