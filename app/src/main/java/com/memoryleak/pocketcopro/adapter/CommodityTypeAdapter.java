package com.memoryleak.pocketcopro.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.fragment.CommodityTypeFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommodityTypeAdapter extends FragmentStateAdapter {
    private final Map<Commodity.Type, CommodityTypeFragment> fragments = new HashMap<>();

    public CommodityTypeAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public CommodityTypeAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public CommodityTypeAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Commodity.Type type = Commodity.Type.values()[position];
        fragments.putIfAbsent(type, new CommodityTypeFragment(type));
        return Objects.requireNonNull(fragments.get(type));
    }

    @Override
    public int getItemCount() {
        return Commodity.Type.values().length;
    }


}
