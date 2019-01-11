package com.example.quoteme;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.UserData.UserContract;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener,
        GestureDetector.OnGestureListener, CompoundButton.OnCheckedChangeListener {

    Button buttonSubmit;
    private EditText userFirstName, userSurname, userEmail, userPassword, userConfirmPassword;
    private GestureDetectorCompat gestureDetector;
    Switch premiumSwitch;
    private int premiumValue;

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
        premiumSwitch = findViewById(R.id.switchPremium);
        premiumSwitch.setChecked(false);

        gestureDetector = new GestureDetectorCompat(this,this);
        premiumSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSubmit){
            addUser();
        }
    }

    private void addUser() {

        //Fill DB table with values
        String userFirstNameString = userFirstName.getText().toString().trim();
        String userSurnameString = userSurname.getText().toString().trim();
        String userEmailString = userEmail.getText().toString().trim();
        String userPasswordString = userPassword.getText().toString().trim();
        String userPasswordConfirmString = userConfirmPassword.getText().toString().trim();

        boolean valueChecker;
        if (userFirstNameString.isEmpty() ||
                userSurnameString.isEmpty() ||
                userEmailString.isEmpty() ||
                userPasswordString.isEmpty() ||
                userPasswordConfirmString.isEmpty()) {
            valueChecker = false;
        } else {
            valueChecker = true;
        }

        if (valueChecker == false) {
            userSurname.setError("Required field");
            userEmail.setError("Required field");
            userPassword.setError("Required field");
            userConfirmPassword.setError("Required field");
            userFirstName.setError("Required field");
        }

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_USERS_FIRSTNAME, userFirstNameString);
        values.put(UserContract.UserEntry.COLUMN_USERS_SURNAME, userSurnameString);
        values.put(UserContract.UserEntry.COLUMN_USERS_EMAIL, userEmailString);
        values.put(UserContract.UserEntry.COLUMN_USERS_PASSWORD, userPasswordString);
        values.put(UserContract.UserEntry.COLUMN_USERS_PREMIUM, premiumValue);

        //checks to see if the method returned true or false
        Boolean userExists = checkIfUserAlreadyExists(userEmailString);
        if (valueChecker == true) {
            if (userPasswordString.equals(userPasswordConfirmString)) {
                //if it returns false or the user doesn't exist, then move onto next statement
                if (userExists == false && valueChecker == true) {
                    Uri newUri = getContentResolver().insert(UserContract.UserEntry.CONTENT_URI, values);
                    if (newUri == null) {
                        Toasty.error(this, "Error creating account", Toast.LENGTH_LONG).show();
                    } else {
                        Toasty.success(this, "Successfully registered", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(this, LoginActivity.class);
                        startActivity(i);
                    }
                } else {
                    Toasty.error(this, "A user already exists with that email address. Choose another",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toasty.error(this, "Password do not match", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Boolean checkIfUserAlreadyExists(String emailAddress){

        Boolean hasUserBeenFound = false;

        String [] project = {"email"};
        String selection = "email=?";
        String [] selectionArgs = {emailAddress};

        Cursor cursor = getContentResolver().query(UserContract.UserEntry.CONTENT_URI,
                project,
                selection,
                selectionArgs,
                null
        );

        if(cursor.moveToFirst()){
            hasUserBeenFound = true;
            cursor.close();
        }
        return hasUserBeenFound;
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            premiumValue = 1;
        } else {
            premiumValue = 0;
        }
    }
}
