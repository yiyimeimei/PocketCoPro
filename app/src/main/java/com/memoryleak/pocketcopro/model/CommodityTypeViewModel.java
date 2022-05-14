package com.memoryleak.pocketcopro.model;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.util.Constant;
import com.memoryleak.pocketcopro.util.PageRequest;
import com.memoryleak.pocketcopro.util.PageResponse;
import com.memoryleak.pocketcopro.util.RestException;
import com.memoryleak.pocketcopro.util.Result;
import com.memoryleak.pocketcopro.util.SortCondition;
import com.memoryleak.pocketcopro.util.Utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class CommodityTypeViewModel extends PageListViewModel<Commodity> {
    private static final int COMMODITY_PAGE_SIZE = 20;
    private static final List<SortCondition> CONDITIONS = Arrays.asList(SortCondition.DISTANCE_ASCENDING, SortCondition.COMMODITY_ID_ASCENDING);
    private Location location;
    private String searchText = "";
    private Commodity.Type type = null;

    public CommodityTypeViewModel() {
        super(new PagedList.Config.Builder().setPageSize(COMMODITY_PAGE_SIZE).setEnablePlaceholders(false).build());
    }

    @Override
    public DataSource<Commodity, Commodity> dataSource() {
        if (location == null) return Utility.emptyDataSource();
        return new ItemKeyedDataSource<Commodity, Commodity>() {
            @Override
            public void loadInitial(@NonNull LoadInitialParams<Commodity> params, @NonNull LoadInitialCallback<Commodity> callback) {
                Map<String, Object> requestContent = constructRequestContent();
                PageRequest<Commodity, Map<String, Object>> request = new PageRequest<>(params.requestedLoadSize, CONDITIONS, requestContent);
                PageResponse<Commodity> response = retrieveCommodityData(request);
                if (response != null)
                    callback.onResult(response.getData(), response.getPosition(), response.getTotal());
                else callback.onResult(Collections.emptyList());
                dataRefreshing.postValue(false);
            }

            @Override
            public void loadAfter(@NonNull LoadParams<Commodity> params, @NonNull LoadCallback<Commodity> callback) {
                Map<String, Object> requestContent = constructRequestContent();
                PageRequest<Commodity, Map<String, Object>> request = new PageRequest<>(params.requestedLoadSize, PageRequest.Direction.AFTER, CONDITIONS, params.key, requestContent);
                PageResponse<Commodity> response = retrieveCommodityData(request);
                if (response != null) callback.onResult(response.getData());
                else callback.onResult(Collections.emptyList());
                dataRefreshing.postValue(false);
            }

            @Override
            public void loadBefore(@NonNull LoadParams<Commodity> params, @NonNull LoadCallback<Commodity> callback) {
                Map<String, Object> requestContent = constructRequestContent();
                PageRequest<Commodity, Map<String, Object>> request = new PageRequest<>(params.requestedLoadSize, PageRequest.Direction.BEFORE, CONDITIONS, params.key, requestContent);
                PageResponse<Commodity> response = retrieveCommodityData(request);
                if (response != null) callback.onResult(response.getData());
                else callback.onResult(Collections.emptyList());
                dataRefreshing.postValue(false);
            }

            @NonNull
            @Override
            public Commodity getKey(@NonNull Commodity item) {
                return item;
            }
        };
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Commodity.Type getType() {
        return type;
    }

    public void setType(Commodity.Type type) {
        this.type = type;
    }

    @Override
    public void initialize() {
        if (location == null) return;
        super.initialize();
    }

    private Map<String, Object> constructRequestContent() {
        Map<String, Object> requestContent = new HashMap<>();
        requestContent.put(Constant.SEARCH, searchText);
        requestContent.put(Constant.LATITUDE, location.getLatitude());
        requestContent.put(Constant.LONGITUDE, location.getLongitude());
        requestContent.put(Constant.COMMODITY_TYPE, type);
        Log.d(Constant.NAME, "request for commodity: " + requestContent);
        return requestContent;
    }

    private PageResponse<Commodity> retrieveCommodityData(PageRequest<Commodity, Map<String, Object>> request) {
        try {
            Response<Result<PageResponse<Commodity>>> response = Constant.COMMODITY_NETWORK_SERVICE.findByName(request).execute();
            return Utility.retrieveData(response);
        } catch (RestException | IOException e) {
            exception.postValue(e);
            Log.e(Constant.NAME, "retrieve commodity data fail", e);
        }
        return null;
    }
}
