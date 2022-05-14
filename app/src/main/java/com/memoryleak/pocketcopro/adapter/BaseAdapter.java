package com.memoryleak.pocketcopro.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements ItemClickable<T>, ItemBindAware<T> {
    private final Map<Integer, OnItemClickListener<T>> onItemClickListenerMap = new HashMap<>();
    private OnBindItemListener<T> onBindItemListener = null;

    public abstract T getItemData(int position);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        onItemClickListenerMap.forEach((viewId, listener) -> {
            View view = holder.itemView;
            if (viewId != null) view = view.findViewById(viewId);
            view.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(v, getItemData(holder.getAdapterPosition()));
            });
        });
        if (onBindItemListener != null)
            onBindItemListener.onBindItem(getItemData(position), position);
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
