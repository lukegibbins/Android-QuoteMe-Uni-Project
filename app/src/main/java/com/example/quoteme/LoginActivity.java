package com.example.quoteme;

import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quoteme.QuoteData.QuoteContract;
import com.example.quoteme.QuoteData.QuoteDbHelper;
import com.example.quoteme.UserData.UserContract;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textSignUpNow;
    Button buttonLogin;
    QuoteDbHelper db;
    EditText loginUsername, loginPassword;

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

    private void logUserIn(){
        Boolean isAuthenticated = isAuthenticated(loginUsername.getText().toString().trim());

        if(isAuthenticated == true) {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        } else {
            Toasty.error(this, "Incorrect username or password", Toast.LENGTH_LONG).show();
        }
    }

    private Boolean isAuthenticated(String emailAddress){
        Boolean isAuthenticated = false;

        String [] project = {"email, password"};
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

            String emailString = cursor.getString(emailColumnIndex);
            String passwordString = cursor.getString(passwordColumnIndex);

            if(emailString.equals(loginUsername.getText().toString().trim()) &&
                    passwordString.equals(loginPassword.getText().toString().trim())){
                isAuthenticated = true;
            }
        }
        return isAuthenticated;
    }
}
