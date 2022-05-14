package com.memoryleak.pocketcopro.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialContainerTransform;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.adapter.CommodityItemAdapter;
import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.data.Order;
import com.memoryleak.pocketcopro.data.Point;
import com.memoryleak.pocketcopro.databinding.FragmentCommodityDetailBinding;
import com.memoryleak.pocketcopro.model.CommodityDetailViewModel;
import com.memoryleak.pocketcopro.model.MainViewModel;
import com.memoryleak.pocketcopro.util.Application;
import com.memoryleak.pocketcopro.util.Constant;
import com.memoryleak.pocketcopro.util.Function;
import com.memoryleak.pocketcopro.util.RestCallback;
import com.memoryleak.pocketcopro.util.Utility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommodityDetailFragment extends Fragment {
    private CommodityDetailViewModel model = null;
    private MainViewModel mainViewModel = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MaterialContainerTransform transition = new MaterialContainerTransform();
        transition.setScrimColor(Color.TRANSPARENT);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        if (mainViewModel == null)
            mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        if (model == null)
            model = new ViewModelProvider(this).get(CommodityDetailViewModel.class);
        model.setCommodity(mainViewModel.selectedCommodity());
        mainViewModel.selectCommodity(null);
        model.setPinnedItemId(mainViewModel.getScannedItemId());
        mainViewModel.setScannedItemId(null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        postponeEnterTransition();
        setHasOptionsMenu(true);
        FragmentCommodityDetailBinding binding = FragmentCommodityDetailBinding.inflate(inflater, container, false);
        Commodity commodity = model.getCommodity();
        binding.setCommodity(commodity);
        binding.container.setTransitionName(commodity.getCommodityId());
        Glide.with(this)
                .load(Constant.PICTURE_URL + commodity.getIconId())
                .error(R.drawable.error)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(binding.icon);
        SwipeRefreshLayout swipeRefreshLayout = binding.refresher;
        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.progress_blue, R.color.progress_red, R.color.progress_green);
        RecyclerView recyclerView = binding.itemList;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        CommodityItemAdapter adapter = new CommodityItemAdapter(Collections.emptyList());
        adapter.setOnBindItemListener((data, position) -> {
            if (Objects.equals(data.getItemId(), model.getPinnedItemId()))
                recyclerView.scrollToPosition(position);
        });
        adapter.setOnItemClickListener(R.id.navigate, (view, data) -> {
            Point point = data.getLocation();
            Uri uri = Uri.parse("geo:" + point.getY() + ',' + point.getX());
            Log.i(Constant.NAME, "navigate to: " + uri.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.navigate_to)));
        });
        adapter.setOnItemClickListener(R.id.order, (view, data) -> Utility.confirmOrder(requireContext(), Utility.helpHint(model.getCommodity()), () -> {
            Map<String, Object> request = new HashMap<>();
            request.put(Constant.COMMODITY_ID, data.getCommodityId());
            request.put(Constant.COMMODITY_ITEM_ID, data.getItemId());
            request.put(Constant.USER_ID, Application.user.getUserId());
            Function hook = Utility.loading(requireContext());
            Constant.ORDER_NETWORK_SERVICE.create(request).enqueue((RestCallback<Order>) (order, e) -> {
                hook.apply();
                if (e != null) {
                    Log.e(Constant.NAME, "create order fail", e);
                    Snackbar.make(swipeRefreshLayout, R.string.network_error, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.confirm, v -> {
                            })
                            .show();
                    return;
                }
                mainViewModel.selectOrder(order);
                NavController controller = NavHostFragment.findNavController(this);
                controller.navigate(R.id.action_place_order);
            });
        }));
        recyclerView.setAdapter(adapter);
        model.commodityItems().observe(getViewLifecycleOwner(), adapter::setCommodityItems);
        model.isDataRefreshing().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);
        model.exception().observe(getViewLifecycleOwner(), e -> {
            if (e != null)
                Snackbar.make(binding.getRoot(), R.string.network_error, Snackbar.LENGTH_SHORT).setAction(R.string.refresh, v -> refresh()).show();
        });
        refresh();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.help, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.help)
                    .setMessage(Utility.helpHint(model.getCommodity()))
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    })
                    .create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        model.refresh(mainViewModel.location().getValue());
    }
}
