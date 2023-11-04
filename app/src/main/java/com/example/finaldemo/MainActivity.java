package com.example.finaldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.appwrite.coroutines.CoroutineCallback;

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
            Appwrite.onGetAccount(new CoroutineCallback<>((result, error) -> {

                Intent intent;

                if (error != null) {  /* User isn't logged in. */
                    intent = new Intent(this, AuthActivity.class);
                } else {              /* User is logged in.    */
                    intent = new Intent(this, HomeActivity.class);

                    /* This will pass the account name to HomeActivity */
                    intent.putExtra("name", result.getName());
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                this.finish();
            }));
        }
    }
}