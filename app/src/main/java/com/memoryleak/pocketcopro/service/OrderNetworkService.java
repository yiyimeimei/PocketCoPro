package com.memoryleak.pocketcopro.service;

import com.memoryleak.pocketcopro.data.Order;
import com.memoryleak.pocketcopro.util.PageRequest;
import com.memoryleak.pocketcopro.util.PageResponse;
import com.memoryleak.pocketcopro.util.Result;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrderNetworkService {
    @POST("order/user/getAllOrders")
    Call<Result<PageResponse<Order>>> findByUserId(@Body PageRequest<Order, Map<String, Object>> request);

    @POST("order/user/getOrderDetail")
    Call<Result<Order>> getOrderDetail(@Body Map<String, Object> request);

    @POST("order/user/create")
    Call<Result<Order>> create(@Body Map<String, Object> request);

    @POST("order/user/buy")
    Call<Result<Order>> buy(@Body Map<String, Object> request);

    @POST("order/user/deposit")
    Call<Result<Order>> deposit(@Body Map<String, Object> request);

    @POST("order/user/confirmDelivery")
    Call<Result<Order>> confirmDelivery(@Body Map<String, Object> request);

    @POST("order/user/returnProduct")
    Call<Result<Order>> returnProduct(@Body Map<String, Object> request);

    @POST("order/user/continueRent")
    Call<Result<Order>> continueRent(@Body Map<String, Object> request);

    @POST("order/user/unlock")
    Call<Result<Order>> unlock(@Body Map<String, Object> request);
}
