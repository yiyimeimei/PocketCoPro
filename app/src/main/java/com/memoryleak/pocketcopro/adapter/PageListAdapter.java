package com.memoryleak.pocketcopro.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public abstract class PageListAdapter<T, VH extends RecyclerView.ViewHolder> extends PagedListAdapter<T, VH> implements ItemClickable<T>, ItemBindAware<T> {
    private final Map<Integer, OnItemClickListener<T>> onItemClickListenerMap = new HashMap<>();
    private OnBindItemListener<T> onBindItemListener = null;

    protected PageListAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    protected PageListAdapter(@NonNull AsyncDifferConfig<T> config) {
        super(config);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        onItemClickListenerMap.forEach((viewId, listener) -> {
            View view = holder.itemView;
            if (viewId != null) view = view.findViewById(viewId);
            view.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(v, getItem(holder.getAdapterPosition()));
            });
        });
        if (onBindItemListener != null) onBindItemListener.onBindItem(getItem(position), position);
    }

    @Override
    public OnItemClickListener<T> getOnItemClickListener(Integer viewId) {
        return onItemClickListenerMap.get(viewId);
    }

    @Override
    public void setOnItemClickListener(Integer viewId, OnItemClickListener<T> onItemClickListener) {
        onItemClickListenerMap.put(viewId, onItemClickListener);
    }

    public OnBindItemListener<T> getOnBindItemListener() {
        return onBindItemListener;
    }

    public void setOnBindItemListener(OnBindItemListener<T> onBindItemListener) {
        this.onBindItemListener = onBindItemListener;
    }
}
