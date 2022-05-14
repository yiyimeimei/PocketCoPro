package com.memoryleak.pocketcopro.model;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.memoryleak.pocketcopro.data.Order;
import com.memoryleak.pocketcopro.util.Constant;
import com.memoryleak.pocketcopro.util.RestCallback;
import com.memoryleak.pocketcopro.util.Utility;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailViewModel extends ViewModel {
    private String orderId = null;
    private final MutableLiveData<Order> orderDetail = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> dataRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<Exception> exception = new MutableLiveData<>(null);
    private final MutableLiveData<Order.Action> currentAction = new MutableLiveData<>(null);

    public OrderDetailViewModel() {
    }

    public LiveData<Order> orderDetail() {
        return orderDetail;
    }

    public LiveData<Boolean> isDataRefreshing() {
        return dataRefreshing;
    }

    public LiveData<Order.Action> currentAction() {
        return currentAction;
    }

    public LiveData<Exception> exception() {
        return exception;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void refresh() {
        dataRefreshing.postValue(true);
        Map<String, Object> request = new HashMap<>();
        request.put(Constant.ORDER_ID, orderId);
        Log.i(Constant.NAME, "retrieve order detail for " + orderId);
        Constant.ORDER_NETWORK_SERVICE.getOrderDetail(request).enqueue((RestCallback<Order>) (data, e) -> {
            if (e != null) {
                Log.e(Constant.NAME, "retrieve order detail fail", e);
                exception.postValue(e);
            } else {
                Collections.reverse(data.getOrderEventList());
                Log.i(Constant.NAME, "retrieved order detail: " + data);
                orderDetail.postValue(data);
                dataRefreshing.postValue(false);
            }
        });
    }

    public void act(Order.Action action) {
        currentAction.postValue(action);
        Map<String, Object> request = new HashMap<>();
        request.put(Constant.ORDER_ID, orderId);
        RestCallback<Order> callback = (data, e) -> {
            currentAction.postValue(null);
            if (e != null) {
                exception.postValue(e);
                Log.e(Constant.NAME, "perform action " + action + " for order " + orderId + " failed", e);
                return;
            }
            orderDetail.postValue(data);
        };
        switch (action.getApiName()) {
            case buy:
                Constant.ORDER_NETWORK_SERVICE.buy(request).enqueue(callback);
                break;
            case deposit:
                Constant.ORDER_NETWORK_SERVICE.deposit(request).enqueue(callback);
                break;
            case confirmDelivery:
                Constant.ORDER_NETWORK_SERVICE.confirmDelivery(request).enqueue(callback);
                break;
            case returnProduct:
                Constant.ORDER_NETWORK_SERVICE.returnProduct(request).enqueue(callback);
                break;
            case continueRent:
                Constant.ORDER_NETWORK_SERVICE.continueRent(request).enqueue(callback);
                break;
            case unlock:
                Constant.ORDER_NETWORK_SERVICE.unlock(request).enqueue((RestCallback<Order>) (data, e) -> {
                    if (e == null) {
                        Thread thread = new Thread(() -> {
                            try (BluetoothSocket socket = Utility.connectBluetooth(Constant.BLUETOOTH_ADDRESS)) {
                                socket.connect();
                                socket.getOutputStream().write(Constant.UNLOCK);
                                int result = socket.getInputStream().read();
                                Log.i(Constant.NAME, "unlock result: " + result);
                            } catch (IOException exception) {
                                Log.e(Constant.NAME, "unlock fail", exception);
                            } finally {
                                callback.callback(data, e);
                            }
                        });
                        thread.start();
                    } else callback.callback(data, e);
                });
                break;
            default:
                Log.e(Constant.NAME, "cannot handle action " + action.getApiName());
        }
    }
}
