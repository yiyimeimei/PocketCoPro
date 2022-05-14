package com.memoryleak.pocketcopro.model;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.data.Order;

import java.util.Objects;

public class MainViewModel extends ViewModel {
    private Commodity selectedCommodity = null;
    private Order selectedOrder = null;
    private String scannedItemId = null;
    private final MutableLiveData<Location> location = new MutableLiveData<>(null);
    private final MutableLiveData<String> searchText = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> actionMenuShown = new MutableLiveData<>(false);

    public MainViewModel() {
    }

    public void updateSearch(String search) {
        if (!Objects.equals(search, searchText.getValue())) searchText.postValue(search);
    }

    public LiveData<String> searchText() {
        return searchText;
    }

    public MutableLiveData<Boolean> actionMenuShown() {
        return actionMenuShown;
    }

    public MutableLiveData<Location> location() {
        return location;
    }

    public Commodity selectedCommodity() {
        return selectedCommodity;
    }

    public void selectCommodity(Commodity commodity) {
        selectedCommodity = commodity;
    }

    public Order selectedOrder() {
        return selectedOrder;
    }

    public void selectOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public String getScannedItemId() {
        return scannedItemId;
    }

    public void setScannedItemId(String scannedItemId) {
        this.scannedItemId = scannedItemId;
    }
}
