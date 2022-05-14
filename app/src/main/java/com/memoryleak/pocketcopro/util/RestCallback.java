package com.memoryleak.pocketcopro.util;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@FunctionalInterface
public interface RestCallback<T> extends Callback<Result<T>> {
    @Override
    default void onResponse(@NonNull Call<Result<T>> call, @NonNull Response<Result<T>> response) {
        if (!response.isSuccessful()) {
            callback(null, new RestException(response.code()));
            return;
        }
        Result<T> result = response.body();
        if (result == null) {
            callback(null, new RestException(RestException.NULL_RESPONSE_MESSAGE, RestException.NULL_RESPONSE));
            return;
        }
        Result.StatusCode status = result.getStatusCode();
        RestException e = null;
        if (status != Result.StatusCode.SUCCESS)
            e = new RestException(status.name(), status.ordinal());
        T data = result.getData();
        callback(data, e);
    }

    @Override
    default void onFailure(@NonNull Call<Result<T>> call, @NonNull Throwable t) {
        callback(null, new RestException(t, RestException.NULL_RESPONSE));
    }

    void callback(T data, RestException e);
}
