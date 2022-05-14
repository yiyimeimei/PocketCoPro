package com.memoryleak.pocketcopro.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.memoryleak.pocketcopro.adapter.OrderEventAdapter;
import com.memoryleak.pocketcopro.data.Order;
import com.memoryleak.pocketcopro.databinding.ActionButtonBinding;
import com.memoryleak.pocketcopro.databinding.FragmentOrderDetailBinding;
import com.memoryleak.pocketcopro.model.MainViewModel;
import com.memoryleak.pocketcopro.model.OrderDetailViewModel;
import com.memoryleak.pocketcopro.util.Constant;
import com.memoryleak.pocketcopro.util.Utility;
import com.memoryleak.pocketcopro.view.DividerDecoration;

import java.util.Collections;

public class OrderDetailFragment extends Fragment {
    private OrderDetailViewModel model = null;
    private MainViewModel mainViewModel = null;
    private AlertDialog dialog = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MaterialContainerTransform transition = new MaterialContainerTransform();
        transition.setScrimColor(Color.TRANSPARENT);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        if (model == null)
            model = new ViewModelProvider(this).get(OrderDetailViewModel.class);
        if (mainViewModel == null)
            mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        model.setOrderId(mainViewModel.selectedOrder().getOrderId());
        model.refresh();
        dialog = new AlertDialog.Builder(requireContext())
                .setView(R.layout.loading)
                .setCancelable(false)
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        postponeEnterTransition();
        setHasOptionsMenu(true);
        FragmentOrderDetailBinding binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        binding.summary.setTransitionName(model.getOrderId());
        binding.setFormatter(Constant.FORMATTER);
        SwipeRefreshLayout swipeRefreshLayout = binding.refresher;
        swipeRefreshLayout.setOnRefreshListener(model::refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.progress_blue, R.color.progress_red, R.color.progress_green);
        model.isDataRefreshing().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);
        ViewGroup actions = binding.actions;
        RecyclerView events = binding.events;
        OrderEventAdapter adapter = new OrderEventAdapter(Collections.emptyList());
        model.orderDetail().observe(getViewLifecycleOwner(), orderDetail -> {
            binding.setOrder(orderDetail);
            if (orderDetail == null) return;
            actions.removeAllViews();
            orderDetail.getNextActionList().forEach(action -> {
                Log.i(Constant.NAME, "add action button for " + action);
                ActionButtonBinding buttonBinding = ActionButtonBinding.inflate(inflater, actions, true);
                buttonBinding.setActionContent(action);
                buttonBinding.action.setOnClickListener(v -> act(action));
            });
            adapter.setEvents(orderDetail.getOrderEventList());
            Glide.with(this)
                    .load(Constant.PICTURE_URL + orderDetail.getIconId())
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
        });
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        events.setLayoutManager(manager);
        events.setAdapter(adapter);
        events.addItemDecoration(new DividerDecoration(requireContext(), manager.getOrientation()));
        model.currentAction().observe(getViewLifecycleOwner(), action -> {
            if (action != null) {
                dialog.setTitle(action.getUserName());
                dialog.show();
            } else dialog.cancel();
        });
        model.exception().observe(getViewLifecycleOwner(), e -> {
            if (e != null)
                Snackbar.make(binding.getRoot(), R.string.network_error, Snackbar.LENGTH_SHORT).setAction(R.string.refresh, v0 -> model.refresh()).show();
        });
        return binding.getRoot();
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
                    .setMessage(Utility.helpHint(model.orderDetail().getValue()))
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    })
                    .create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void act(Order.Action action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(action.getUserName())
                .setMessage(action.getUserName())
                .setPositiveButton(R.string.confirm, (dialog, which) -> model.act(action))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                })
                .setCancelable(true)
                .show();
    }
}
