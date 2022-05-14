package com.memoryleak.pocketcopro.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.data.Order;
import com.memoryleak.pocketcopro.databinding.ItemOrderBinding;
import com.memoryleak.pocketcopro.util.Constant;

import java.util.Objects;

public class OrderAdapter extends PageListAdapter<Order, OrderAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<Order> CALLBACK = new DiffUtil.ItemCallback<Order>() {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return Objects.equals(oldItem.getOrderId(), newItem.getOrderId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public OrderAdapter() {
        super(CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Order order = getItem(position);
        holder.binding.setOrder(order);
        if (order == null) return;
        holder.itemView.setTransitionName(order.getOrderId());
        Glide.with(holder.itemView)
                .load(Constant.PICTURE_URL + order.getIconId())
                .error(R.drawable.error)
                .into(holder.binding.icon);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderBinding binding;

        public ViewHolder(@NonNull ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
