package com.example.quoteme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteDbHelper;
import com.example.quoteme.UserData.UserContract;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textSignUpNow;
    Button buttonLogin;
    QuoteDbHelper db;
    EditText loginUsername, loginPassword;

    SharedPreferences sharedPreferences;
    public static final String SHARED_PREF_FILE = "com.example.quoteme";
    public static final String USERNAME = "username";
    public static final String FIRST_NAME = "firstName";
    public static final String SURNAME = "surname";
    public static final String EMAIL = "email";
    public static final String PREMIUM = "premium";
    public static final String ID = "id";

    private String userFirstName;
    private String userSurname;
    private String usersEmail;
    private String usersPremiumAccessCode;
    private String users_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new QuoteDbHelper(this);

        textSignUpNow = findViewById(R.id.textSignUpNow);
        textSignUpNow.setOnClickListener(this);

        loginUsername = findViewById(R.id.editEmail);
        loginPassword = findViewById(R.id.editPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        if(v == textSignUpNow) {
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        }
         else if (v == buttonLogin){
            logUserIn();
        }
    }


    //Method to try and log user in
    private void logUserIn(){

        //This boolean finds out if the user and password that the user has entered are correct
        Boolean isAuthenticated = isAuthenticated(loginUsername.getText().toString().trim());

        //If the credentials are correct, pass through user data to a shared preferences file to use
        //In the program
        if(isAuthenticated == true) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USERNAME, loginUsername.getText().toString().trim());
            editor.putString(FIRST_NAME, userFirstName);
            editor.putString(SURNAME, userSurname);
            editor.putString(EMAIL, usersEmail);
            editor.putString(PREMIUM, usersPremiumAccessCode);
            editor.putString(ID, users_Id);
            editor.apply();
            editor.commit();

            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        } else {
            Toasty.error(this, "Incorrect username or password", Toast.LENGTH_LONG).show();
        }
    }


    //Decides whether the users credentials are correct by querying the database
    //Using the parameters provided at login.
    private Boolean isAuthenticated(String emailAddress){
        Boolean isAuthenticated = false;

        String [] project = {"email, password, firstName, surname, premium, _id"};
        String selection = "email=?";
        String [] selectionArgs = {emailAddress};

        Cursor cursor = getContentResolver().query(UserContract.UserEntry.CONTENT_URI,
                project,
                selection,
                selectionArgs,
                null
        );

        if(cursor.moveToFirst()){
            int emailColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERS_EMAIL);
            int passwordColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERS_PASSWORD);
            int firstNameColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERS_FIRSTNAME);
            int surnameColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERS_SURNAME);
            int premiumColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERS_PREMIUM);
            int idColumnIndex = cursor.getColumnIndex("_id");

            String emailString = cursor.getString(emailColumnIndex);
            String passwordString = cursor.getString(passwordColumnIndex);
            String userFirstNameString = cursor.getString(firstNameColumnIndex);
            String userSurnameString = cursor.getString(surnameColumnIndex);

            int premiumCode = cursor.getInt(premiumColumnIndex);
            String premiumCodeString = String.valueOf(premiumCode);

            int userId = cursor.getInt(idColumnIndex);
            String userIdString = String.valueOf(userId);

            userFirstName = userFirstNameString;
            userSurname = userSurnameString;
            usersEmail = emailString;
            usersPremiumAccessCode = premiumCodeString;
            users_Id = userIdString;

            //Do the passwords match? if so, authenticated == true
            if(emailString.equals(loginUsername.getText().toString().trim()) &&
                    passwordString.equals(loginPassword.getText().toString().trim())){
                isAuthenticated = true;
            }
        }
        return isAuthenticated;
    }

}
