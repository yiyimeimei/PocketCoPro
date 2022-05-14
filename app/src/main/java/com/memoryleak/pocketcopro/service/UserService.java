package com.memoryleak.pocketcopro.service;

import com.memoryleak.pocketcopro.data.User;
import com.memoryleak.pocketcopro.util.Result;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("user/loginOrRegisterNormal")
    Call<Result<User>> loginOrRegister(@Body Map<String, String> authentication);

    @POST("user/getProfile")
    Call<Result<User>> getProfile(@Body Map<String, Object> request);
}
