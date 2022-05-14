package com.memoryleak.pocketcopro.model;

import androidx.lifecycle.ViewModel;

import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {
    private final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-]*$");
    private final Pattern PASSWORD_PATTERN = Pattern.compile("^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z]).*$");

    public boolean checkUsername(String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }

    public boolean checkPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}