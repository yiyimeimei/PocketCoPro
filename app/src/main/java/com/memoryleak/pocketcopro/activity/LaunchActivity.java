package com.memoryleak.pocketcopro.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.memoryleak.pocketcopro.BuildConfig;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.data.User;
import com.memoryleak.pocketcopro.util.Application;
import com.memoryleak.pocketcopro.util.Constant;

import java.util.LinkedList;
import java.util.List;

public class LaunchActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        initializeOCR();
        List<String> permissions = checkPermissions();
        if (permissions.isEmpty()) checkUser();
        else ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 0);
    }

    private List<String> checkPermissions() {
        List<String> required = new LinkedList<>();
        for (String permission : Constant.PERMISSIONS)
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED)
                required.add(permission);
        return required;
    }

    private void checkUser() {
        new Thread(() -> {
            SharedPreferences userPreferences = getSharedPreferences(Constant.USER_PREFERENCE_NAME, MODE_PRIVATE);
            String data = userPreferences.getString("user", null);
            if (data == null) {
                runOnUiThread(() -> {
                    startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                    finish();
                });
                return;
            }
            User user = Constant.GSON.fromJson(data, User.class);
            synchronized (Application.class) {
                Application.user = user;
            }
            runOnUiThread(() -> {
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                finish();
            });
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (BuildConfig.DEBUG && requestCode != 0)
            throw new AssertionError("unknown request of permissions");
        for (int result : grantResults)
            if (result != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(LaunchActivity.this)
                        .setTitle(R.string.permission_miss)
                        .setMessage(R.string.exit_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, (dialog, which) -> finish())
                        .create()
                        .show();
                return;
            }
        checkUser();
    }

    private void initializeOCR() {
        Log.i(Constant.NAME, "initialize ocr");
        OCR.getInstance(getApplicationContext()).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                Log.i(Constant.NAME, "ocr initialize success");
            }

            @Override
            public void onError(OCRError ocrError) {
                Log.e(Constant.NAME, "ocr initialize fail", ocrError);
            }
        }, getApplicationContext(), Constant.OCR_API_KEY, Constant.OCR_SECRET_KEY);
    }
}
