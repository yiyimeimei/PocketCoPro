package com.memoryleak.pocketcopro.adapter;

public interface ItemBindAware<T> {
    OnBindItemListener<T> getOnBindItemListener();

    void setOnBindItemListener(OnBindItemListener<T> onBindItemListener);
}
