package com.memoryleak.pocketcopro.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.memoryleak.pocketcopro.data.User;
import com.memoryleak.pocketcopro.util.Application;
import com.memoryleak.pocketcopro.util.Constant;
import com.memoryleak.pocketcopro.util.RestCallback;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends ViewModel {
    private String userId;
    private final MutableLiveData<User> user = new MutableLiveData<>(Application.user);
    private final MutableLiveData<Exception> exception = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>(false);

    public UserViewModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LiveData<User> currentUser() {
        return user;
    }

    public LiveData<Exception> exception() {
        return exception;
    }

    public LiveData<Boolean> isRefreshing() {
        return refreshing;
    }

    public void refresh() {
        refreshing.postValue(true);
        Map<String, Object> request = new HashMap<>();
        request.put(Constant.USER_ID, userId);
        Log.i(Constant.NAME, "get user profile for user id: " + userId);
        Constant.USER_SERVICE.getProfile(request).enqueue((RestCallback<User>) (data, e) -> {
            if (e != null) exception.postValue(e);
            else user.postValue(data);
            refreshing.postValue(false);
            Log.i(Constant.NAME, "user profile retrieved: " + data);
        });

    }
}
