package com.example.finaldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import io.appwrite.coroutines.CoroutineCallback;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    EditText nameEditText, emailEditText, passwordEditText;
    Button submitButton, changeStateButton;
    String state = "register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        /* Refer views from UI */
        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        submitButton = findViewById(R.id.submit_button);
        changeStateButton = findViewById(R.id.change_state_button);

        /* Set OnClickListener on our buttons */
        submitButton.setOnClickListener(this);
        changeStateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        /* Extract user input */
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        switch ((view.getId())) {
            case R.id.submit_button:
                if (Objects.equals(state, "register")) {
                    register(email, password, name);
                } else {
                    login(email, password);
                }
                break;

            case R.id.change_state_button:
                changeState();
                break;
        }
    }

    void login(String email, String password) {
        Appwrite.onLogin(email, password, new CoroutineCallback<>((r, error) -> {
            if (error != null) {
                /* Login unsuccessful */
                error.printStackTrace();
                return;
            }

            /* Get current user and redirect to HomeActivity */
            Appwrite.onGetAccount(new CoroutineCallback<>((result, e) -> {

                Intent intent = new Intent(this, HomeActivity.class);

                /* This will pass the account name to HomeActivity */
                intent.putExtra("name", result.getName());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                this.finish();
            }));
        }));
    }

    void register(String email, String password, String name) {
        Appwrite.onCreateAccount(email, password, name, new CoroutineCallback<>((res, err) -> {
            if (err != null) {
                /* Create account unsuccessful */
                err.printStackTrace();
                return;
            }

            /* Log the new user in, and redirect to HomeActivity */
            login(email, password);
        }));
    }

    void changeState() {
        if (Objects.equals(state, "register")) {
            state = "login";
            nameEditText.setVisibility(View.GONE);
            submitButton.setText("Login");
            changeStateButton.setText("Register?");
        } else {
            state = "register";
            nameEditText.setVisibility(View.VISIBLE);
            submitButton.setText("Register");
            changeStateButton.setText("Login?");
        }
    }
}