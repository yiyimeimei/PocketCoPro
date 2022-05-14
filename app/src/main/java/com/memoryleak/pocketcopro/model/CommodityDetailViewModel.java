package com.memoryleak.pocketcopro.model;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.data.CommodityItem;
import com.memoryleak.pocketcopro.util.Constant;
import com.memoryleak.pocketcopro.util.RestCallback;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommodityDetailViewModel extends ViewModel {
    private Location location;
    private Commodity commodity = null;
    private String pinnedItemId = null;
    private final MutableLiveData<List<CommodityItem>> commodityItems = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Exception> exception = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> dataRefreshing = new MutableLiveData<>(true);

    public CommodityDetailViewModel() {
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public String getPinnedItemId() {
        return pinnedItemId;
    }

    public void setPinnedItemId(String pinnedItemId) {
        this.pinnedItemId = pinnedItemId;
    }

    public LiveData<List<CommodityItem>> commodityItems() {
        return commodityItems;
    }

    public LiveData<Exception> exception() {
        return exception;
    }

    public LiveData<Boolean> isDataRefreshing() {
        return dataRefreshing;
    }

    public void refresh(Location location) {
        if (location != null) this.location = location;
        else location = this.location;
        if (commodity == null || location == null) return;
        dataRefreshing.postValue(true);
        Map<String, Object> request = new HashMap<>();
        request.put(Constant.COMMODITY_ID, commodity.getCommodityId());
        request.put(Constant.LATITUDE, location.getLatitude());
        request.put(Constant.LONGITUDE, location.getLongitude());
        request.put(Constant.DISTANCE, Constant.MAXIMUM_DISTANCE_10000);
        Log.i(Constant.NAME, "retrieve commodity detail for " + request);
        Constant.COMMODITY_NETWORK_SERVICE.findByCommodityIdAndNear(request).enqueue((RestCallback<List<CommodityItem>>) (commodityItems, commodityItemsException) -> {
            if (commodityItemsException != null) {
                exception.postValue(commodityItemsException);
                Log.e(Constant.NAME, "retrieve commodity detail items fail", commodityItemsException);
            } else {
                this.commodityItems.postValue(commodityItems);
                Log.i(Constant.NAME, "retrieved commodity items: " + commodityItems);
            }
            dataRefreshing.postValue(false);
        });
    }
}
