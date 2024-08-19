package com.example.campusteamup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.widget.Space;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Call_Method.lightActionBar(getWindow());

        firebaseAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firebaseAuth != null && firebaseAuth.getCurrentUser() != null)
                {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
                else{
                    startActivity(new Intent(SplashScreen.this,UserLogin.class));
                }
                finish();
            }
        },1000);
    }


}