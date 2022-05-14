package com.memoryleak.pocketcopro.adapter;

import android.view.View;

public interface ItemClickable<T> {
    default OnItemClickListener<T> getOnItemClickListener() {
        return getOnItemClickListener(null);
    }

    default void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        setOnItemClickListener(null, onItemClickListener);
    }

    OnItemClickListener<T> getOnItemClickListener(Integer viewId);

    void setOnItemClickListener(Integer viewId, OnItemClickListener<T> onItemClickListener);

    @FunctionalInterface
    interface OnItemClickListener<T> {
        void onItemClick(View view, T data);
    }
}
