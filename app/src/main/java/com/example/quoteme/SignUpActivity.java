package com.example.quoteme;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener,
        GestureDetector.OnGestureListener {

    Button buttonSubmit;
    private GestureDetectorCompat gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        buttonSubmit = findViewById(R.id.buttonSignUp);
        buttonSubmit.setOnClickListener(this);

        gestureDetector = new GestureDetectorCompat(this,this);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonSubmit){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
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
