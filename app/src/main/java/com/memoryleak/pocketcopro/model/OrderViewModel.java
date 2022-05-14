package com.memoryleak.pocketcopro.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.memoryleak.pocketcopro.data.Order;
import com.memoryleak.pocketcopro.util.Application;
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

public class OrderViewModel extends PageListViewModel<Order> {
    private static final int ORDER_INITIAL_SIZE = 10;
    private static final int ORDER_PAGE_SIZE = 10;
    private static final List<SortCondition> CONDITIONS = Arrays.asList(SortCondition.CREATE_TIME_DESCENDING, SortCondition.ORDER_ID_ASCENDING);

    public OrderViewModel() {
        super(new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(ORDER_PAGE_SIZE)
                .setInitialLoadSizeHint(ORDER_INITIAL_SIZE)
                .build());
    }

    @Override
    public DataSource<Order, Order> dataSource() {
        return new ItemKeyedDataSource<Order, Order>() {
            @Override
            public void loadInitial(@NonNull LoadInitialParams<Order> params, @NonNull LoadInitialCallback<Order> callback) {
                Log.d(Constant.NAME, "load initial, filter: " + params.requestedInitialKey);
                Map<String, Object> requestContent = new HashMap<>();
                requestContent.put(Constant.USER_ID, Application.user.getUserId());
                PageRequest<Order, Map<String, Object>> request = new PageRequest<>(params.requestedLoadSize, CONDITIONS, requestContent);
                PageResponse<Order> response = retrieveOrderData(request);
                if (response != null)
                    callback.onResult(response.getData(), response.getPosition(), response.getTotal());
                else callback.onResult(Collections.emptyList());
                dataRefreshing.postValue(false);
            }

            @Override
            public void loadAfter(@NonNull LoadParams<Order> params, @NonNull LoadCallback<Order> callback) {
                Log.d(Constant.NAME, "load after, filter: " + params.key);
                Map<String, Object> requestContent = new HashMap<>();
                requestContent.put(Constant.USER_ID, Application.user.getUserId());
                PageRequest<Order, Map<String, Object>> request = new PageRequest<>(params.requestedLoadSize, PageRequest.Direction.AFTER, CONDITIONS, params.key, requestContent);
                PageResponse<Order> response = retrieveOrderData(request);
                if (response != null) callback.onResult(response.getData());
                else callback.onResult(Collections.emptyList());
            }

            @Override
            public void loadBefore(@NonNull LoadParams<Order> params, @NonNull LoadCallback<Order> callback) {
                Log.d(Constant.NAME, "load before, filter: " + params.key);
                Map<String, Object> requestContent = new HashMap<>();
                requestContent.put(Constant.USER_ID, Application.user.getUserId());
                PageRequest<Order, Map<String, Object>> request = new PageRequest<>(params.requestedLoadSize, PageRequest.Direction.BEFORE, CONDITIONS, params.key, requestContent);
                PageResponse<Order> response = retrieveOrderData(request);
                if (response != null) callback.onResult(response.getData());
                else callback.onResult(Collections.emptyList());
            }

            @NonNull
            @Override
            public Order getKey(@NonNull Order item) {
                return item;
            }
        };
    }

    private PageResponse<Order> retrieveOrderData(PageRequest<Order, Map<String, Object>> request) {
        try {
            Response<Result<PageResponse<Order>>> response = Constant.ORDER_NETWORK_SERVICE.findByUserId(request).execute();
            return Utility.retrieveData(response);
        } catch (RestException | IOException e) {
            exception.postValue(e);
            Log.e(Constant.NAME, "retrieve order data fail", e);
        }
        return null;
    }
}
