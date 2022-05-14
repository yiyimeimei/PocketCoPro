package com.memoryleak.pocketcopro.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public abstract class PageListViewModel<T> extends ViewModel {
    private DataSource<T, T> dataSource = null;
    private boolean initialized = false;
    private final LiveData<PagedList<T>> data;
    protected final MutableLiveData<Boolean> dataRefreshing = new MutableLiveData<>(true);
    protected final MutableLiveData<Exception> exception = new MutableLiveData<>(null);

    private class CachedDataSourceFactory extends DataSource.Factory<T, T> {
        @NonNull
        @Override
        public DataSource<T, T> create() {
            if (dataSource == null || dataSource.isInvalid())
                synchronized (this) {
                    if (dataSource == null || dataSource.isInvalid())
                        dataSource = dataSource();
                }
            return dataSource;
        }
    }

    protected PageListViewModel(PagedList.Config config) {
        super();
        DataSource.Factory<T, T> factory = new CachedDataSourceFactory();
        LivePagedListBuilder<T, T> builder = new LivePagedListBuilder<>(factory, config);
        builder.setInitialLoadKey(null);
        this.data = builder.build();
    }

    protected abstract DataSource<T, T> dataSource();

    public LiveData<Boolean> isDataRefreshing() {
        return dataRefreshing;
    }

    public LiveData<PagedList<T>> data() {
        return data;
    }

    public LiveData<Exception> exception() {
        return exception;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize() {
        if (initialized) return;
        initialized = true;
        refresh();
    }

    public void refresh() {
        if (!initialized) initialize();
        dataRefreshing.postValue(true);
        if (dataSource != null) dataSource.invalidate();
    }
}
