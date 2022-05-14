package com.memoryleak.pocketcopro.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.data.User;
import com.memoryleak.pocketcopro.data.UserData;
import com.memoryleak.pocketcopro.model.LoginViewModel;
import com.memoryleak.pocketcopro.util.Application;
import com.memoryleak.pocketcopro.util.Constant;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        Toolbar toolbar = findViewById(R.id.activity_login_toolbar);
        setSupportActionBar(toolbar);
        EditText usernameEditText = findViewById(R.id.login_username);
        TextInputLayout usernameTextLayout = findViewById(R.id.login_username_layout);
        EditText passwordEditText = findViewById(R.id.login_password);
        TextInputLayout passwordTextLayout = findViewById(R.id.login_password_layout);
        usernameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) usernameTextLayout.setError(null);
            else checkUsername(usernameEditText.getText().toString(), usernameTextLayout);
        });
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) passwordTextLayout.setError(null);
            else checkPassword(passwordEditText.getText().toString(), passwordTextLayout);
        });
        FloatingActionButton floatingActionButton = findViewById(R.id.login_confirm);
        floatingActionButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            if (!checkUsername(username, usernameTextLayout)) return;
            String password = passwordEditText.getText().toString();
            if (!checkPassword(password, passwordTextLayout)) return;
            Map<String, String> authentication = new HashMap<>();
            authentication.put(Constant.USERNAME, username);
            authentication.put(Constant.PASSWORD, password);
            SharedPreferences.Editor editor = getSharedPreferences(Constant.USER_PREFERENCE_NAME, MODE_PRIVATE).edit();
            UserData data = new UserData("minbao","123456pp", 1);
            editor.putString("user", Constant.GSON.toJson(data));
            editor.apply();
            User u = new User(data.toString());
            synchronized (Application.class) {
                Application.user = u;
            }
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            /*Constant.USER_SERVICE.loginOrRegister(authentication).enqueue((RestCallback<User>) (data, e) -> {
                if (e != null) {
                    Log.e(Constant.NAME, "login or register fail", e);
                    Snackbar.make(floatingActionButton, R.string.network_error, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.confirm, view -> {
                            })
                            .show();
                    return;
                }
                SharedPreferences.Editor editor = getSharedPreferences(Constant.USER_PREFERENCE_NAME, MODE_PRIVATE).edit();
                editor.putString("user", Constant.GSON.toJson(data));
                editor.apply();
                synchronized (Application.class) {
                    Application.user = data;
                }
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            });*/
        });
    }

    private boolean checkUsername(String username, TextInputLayout textInputLayout) {
        if (!loginViewModel.checkUsername(username))
            textInputLayout.setError(getResources().getText(R.string.username_invalid));
        else {
            textInputLayout.setError(null);
            return true;
        }
        return false;
    }

    private boolean checkPassword(String password, TextInputLayout textInputLayout) {
        if (!loginViewModel.checkPassword(password))
            textInputLayout.setError(getResources().getText(R.string.password_invalid));
        else {
            textInputLayout.setError(null);
            return true;
        }
        return false;
    }
}