package com.memoryleak.pocketcopro.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.data.Order;
import com.memoryleak.pocketcopro.databinding.ItemOrderEventBinding;

import java.util.List;

public class OrderEventAdapter extends RecyclerView.Adapter<OrderEventAdapter.ViewHolder> {
    private List<Order.Event> events;

    public OrderEventAdapter(List<Order.Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderEventBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_order_event, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setEvent(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public List<Order.Event> getEvents() {
        return events;
    }

    public void setEvents(List<Order.Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderEventBinding binding;

        public ViewHolder(@NonNull ItemOrderEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
