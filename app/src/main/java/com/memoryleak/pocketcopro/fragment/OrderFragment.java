package com.memoryleak.pocketcopro.fragment;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialElevationScale;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.adapter.OrderAdapter;
import com.memoryleak.pocketcopro.model.MainViewModel;
import com.memoryleak.pocketcopro.model.OrderViewModel;
import com.memoryleak.pocketcopro.util.Constant;

public class OrderFragment extends Fragment {
    private OrderViewModel model = null;
    private MainViewModel mainViewModel = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainViewModel == null)
            mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        if (model == null)
            model = new ViewModelProvider(this).get(OrderViewModel.class);
        setExitTransition(new MaterialElevationScale(false));
        setReenterTransition(new MaterialElevationScale(true));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        postponeEnterTransition();
        View v = inflater.inflate(R.layout.fragment_order, container, false);
        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.refresher);
        swipeRefreshLayout.setColorSchemeResources(R.color.progress_blue, R.color.progress_red, R.color.progress_green);
        model.isDataRefreshing().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);
        RecyclerView recyclerView = v.findViewById(R.id.list);
        OrderAdapter adapter = new OrderAdapter();
        adapter.setOnItemClickListener((view, data) -> {
            mainViewModel.selectOrder(data);
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(view, view.getTransitionName()).build();
            NavHostFragment.findNavController(this).navigate(R.id.action_order_detail, null, null, extras);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getViewTreeObserver().addOnPreDrawListener(() -> {
            startPostponedEnterTransition();
            return true;
        });
        model.data().observe(getViewLifecycleOwner(), data -> {
            Log.i(Constant.NAME, "data update: " + data);
            adapter.submitList(data);
        });
        swipeRefreshLayout.setOnRefreshListener(model::refresh);
        model.exception().observe(getViewLifecycleOwner(), e -> {
            if (e != null)
                Snackbar.make(v, R.string.network_error, Snackbar.LENGTH_SHORT).setAction(R.string.refresh, v0 -> model.refresh()).show();
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        model.refresh();
    }
}
