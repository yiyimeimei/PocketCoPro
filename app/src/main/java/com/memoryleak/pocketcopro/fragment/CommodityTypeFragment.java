package com.memoryleak.pocketcopro.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialContainerTransform;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.adapter.CommodityAdapter;
import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.model.CommodityTypeViewModel;
import com.memoryleak.pocketcopro.model.MainViewModel;
import com.memoryleak.pocketcopro.util.Constant;

import java.util.Objects;

public class CommodityTypeFragment extends Fragment {
    private final Commodity.Type type;
    private CommodityTypeViewModel model = null;
    private MainViewModel mainViewModel = null;

    public CommodityTypeFragment(Commodity.Type type) {
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainViewModel == null)
            mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        if (model == null)
            model = new ViewModelProvider(this).get(type.name(), CommodityTypeViewModel.class);
        Log.i(Constant.NAME, "load type " + type);
        model.setType(type);
        MaterialContainerTransform transition = new MaterialContainerTransform();
        transition.setScrimColor(Color.TRANSPARENT);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_commodity_type, container, false);
        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.refresher);
        swipeRefreshLayout.setColorSchemeResources(R.color.progress_blue, R.color.progress_red, R.color.progress_green);
        model.isDataRefreshing().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);
        RecyclerView recyclerView = v.findViewById(R.id.list);
        CommodityAdapter adapter = new CommodityAdapter();
        adapter.setOnItemClickListener((view, data) -> {
            mainViewModel.selectCommodity(data);
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(view, view.getTransitionName()).build();
            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.action_commodity_detail, null, null, extras);
        });
        recyclerView.setAdapter(adapter);
        adapter.setOnBindItemListener((data, position) -> {
            Commodity commodity = mainViewModel.selectedCommodity();
            if (commodity == null) return;
            if (Objects.equals(data.getCommodityId(), commodity.getCommodityId()))
                requireParentFragment().startPostponedEnterTransition();
        });
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        model.data().observe(getViewLifecycleOwner(), adapter::submitList);
        swipeRefreshLayout.setOnRefreshListener(model::refresh);
        model.exception().observe(getViewLifecycleOwner(), e -> {
            if (e != null)
                Snackbar.make(v, R.string.network_error, Snackbar.LENGTH_SHORT).setAction(R.string.refresh, v0 -> model.refresh()).show();
        });
        mainViewModel.location().observe(getViewLifecycleOwner(), location -> {
            model.setLocation(location);
            model.initialize();
        });
        mainViewModel.searchText().observe(getViewLifecycleOwner(), s -> {
            Log.i(Constant.NAME, "search name for " + s);
            model.setSearchText(s);
            model.refresh();
        });
        model.initialize();
        return v;
    }
}
