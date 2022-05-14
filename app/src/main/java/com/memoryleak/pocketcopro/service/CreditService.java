package com.memoryleak.pocketcopro.service;

import com.memoryleak.pocketcopro.data.Credit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CreditService {
    @POST("credit/getDimensions")
    Call<Credit> getDimensions(@Body Map<String,String> userId);
}
