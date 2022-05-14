package com.memoryleak.pocketcopro.service;

import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.data.CommodityItem;
import com.memoryleak.pocketcopro.util.PageRequest;
import com.memoryleak.pocketcopro.util.PageResponse;
import com.memoryleak.pocketcopro.util.Result;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CommodityNetworkService {
    @POST("commodity/findByName")
    Call<Result<PageResponse<Commodity>>> findByName(@Body PageRequest<Commodity, Map<String, Object>> request);

    @POST("commodityItem/findByCommodityIdAndSortByDistance")
    Call<Result<List<CommodityItem>>> findByCommodityIdAndNear(@Body Map<String, Object> request);

    @POST("commodityItem/findWithin")
    Call<Result<List<CommodityItem>>> findWithin(@Body Map<String, Object> request);

    @POST("commodityItem/findCommodityByItemId")
    Call<Result<Commodity>> findCommodityByItemId(@Body Map<String, Object> request);
}
