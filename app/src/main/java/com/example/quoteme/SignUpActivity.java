package com.example.quoteme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        buttonSubmit = findViewById(R.id.buttonSignUp);
        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSubmit){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }
}
