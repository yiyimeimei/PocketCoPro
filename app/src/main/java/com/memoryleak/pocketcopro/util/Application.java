package com.memoryleak.pocketcopro.util;

import com.memoryleak.pocketcopro.data.User;

public class Application extends android.app.Application {
    public static User user = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
