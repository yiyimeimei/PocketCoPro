package com.memoryleak.pocketcopro.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.memoryleak.pocketcopro.data.CommodityItem;
import com.memoryleak.pocketcopro.databinding.ItemCommodityItemBinding;

import java.util.List;

public class CommodityItemAdapter extends BaseAdapter<CommodityItem, CommodityItemAdapter.ViewHolder> {
    private List<CommodityItem> commodityItems;

    public CommodityItemAdapter(List<CommodityItem> commodityItems) {
        this.commodityItems = commodityItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommodityItemBinding binding = ItemCommodityItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public CommodityItem getItemData(int position) {
        return commodityItems.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.binding.setItem(commodityItems.get(position));
    }

    @Override
    public int getItemCount() {
        return commodityItems.size();
    }

    public List<CommodityItem> getCommodityItems() {
        return commodityItems;
    }

    public void setCommodityItems(List<CommodityItem> commodityItems) {
        this.commodityItems = commodityItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommodityItemBinding binding;

        public ViewHolder(@NonNull ItemCommodityItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
