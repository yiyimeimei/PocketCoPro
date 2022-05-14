package com.memoryleak.pocketcopro.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.databinding.ItemCommodityBinding;
import com.memoryleak.pocketcopro.util.Constant;

import java.util.Objects;

public class CommodityAdapter extends PageListAdapter<Commodity, CommodityAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<Commodity> CALLBACK = new DiffUtil.ItemCallback<Commodity>() {
        @Override
        public boolean areItemsTheSame(@NonNull Commodity oldItem, @NonNull Commodity newItem) {
            return Objects.equals(oldItem.getCommodityId(), newItem.getCommodityId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Commodity oldItem, @NonNull Commodity newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public CommodityAdapter() {
        super(CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommodityBinding binding = ItemCommodityBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Commodity commodity = getItem(position);
        assert commodity != null;
        holder.binding.setCommodity(commodity);
        holder.itemView.setTransitionName(commodity.getCommodityId());
        holder.itemView.setTag(commodity.getCommodityId());
        Glide.with(holder.itemView)
                .load(Constant.PICTURE_URL + commodity.getIconId())
                .error(R.drawable.error)
                .into(holder.binding.icon);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommodityBinding binding;

        public ViewHolder(@NonNull ItemCommodityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
