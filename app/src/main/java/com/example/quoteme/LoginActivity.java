package com.example.quoteme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textSignUpNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textSignUpNow = findViewById(R.id.textSignUpNow);
        textSignUpNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == textSignUpNow) {
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        }
    }
}
