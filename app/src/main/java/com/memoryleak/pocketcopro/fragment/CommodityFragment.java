package com.memoryleak.pocketcopro.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.adapter.CommodityTypeAdapter;
import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.databinding.FragmentCommodityBinding;
import com.memoryleak.pocketcopro.model.MainViewModel;
import com.memoryleak.pocketcopro.util.Constant;
import com.memoryleak.pocketcopro.util.Function;
import com.memoryleak.pocketcopro.util.RestCallback;
import com.memoryleak.pocketcopro.util.Utility;

import java.util.HashMap;
import java.util.Map;

public class CommodityFragment extends Fragment {
    private MainViewModel mainViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainViewModel == null)
            mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        setExitTransition(new MaterialElevationScale(false));
        setReenterTransition(new MaterialElevationScale(true));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        FragmentCommodityBinding binding = FragmentCommodityBinding.inflate(inflater, container, false);
        ViewPager2 pager = binding.pager;
        pager.setAdapter(new CommodityTypeAdapter(this));
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tab, pager, (tab, position) -> tab.setText(Commodity.Type.values()[position].title));
        mediator.attach();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainViewModel.selectedCommodity() != null) postponeEnterTransition();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mainViewModel.updateSearch(query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setIconifiedByDefault(true);
        searchView.setOnCloseListener(() -> {
            mainViewModel.updateSearch("");
            searchView.clearFocus();
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.scan) {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String content = result.getContents();
            if (content != null) {
                Function onLoaded = Utility.loading(requireContext());
                Map<String, Object> request = new HashMap<>();
                request.put(Constant.COMMODITY_ITEM_ID, content);
                Constant.COMMODITY_NETWORK_SERVICE.findCommodityByItemId(request).enqueue((RestCallback<Commodity>) (commodity, e) -> {
                    if (e != null) {
                        Log.e(Constant.NAME, "find commodity by item id fail, item id: " + content, e);
                    } else {
                        mainViewModel.setScannedItemId(content);
                        mainViewModel.selectCommodity(commodity);
                        NavHostFragment.findNavController(this).navigate(R.id.action_commodity_detail);
                    }
                    onLoaded.apply();
                });
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }
}
