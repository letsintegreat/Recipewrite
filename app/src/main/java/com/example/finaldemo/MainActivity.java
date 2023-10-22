package com.example.finaldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        /* Ensure this is the first time onCreate is called. */
        /* This will prevent appwrite to be initialized multiple times */
        if (savedInstanceState == null) {
            Appwrite.init(this);
            Appwrite.onGetAccount(this);
        }
    }
}