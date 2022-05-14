package com.memoryleak.pocketcopro.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialElevationScale;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.data.Credit;
import com.memoryleak.pocketcopro.data.RadarData;
import com.memoryleak.pocketcopro.databinding.FragmentUserBinding;
import com.memoryleak.pocketcopro.model.UserViewModel;
import com.memoryleak.pocketcopro.util.Application;
import com.memoryleak.pocketcopro.util.Constant;
import com.memoryleak.pocketcopro.view.RadarView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private UserViewModel model = null;
    private RadarView mRadarView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitTransition(new MaterialElevationScale(false));
        setReenterTransition(new MaterialElevationScale(true));
        if (model == null) model = new ViewModelProvider(this).get(UserViewModel.class);
        model.setUserId(Application.user.getUserId());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentUserBinding binding = FragmentUserBinding.inflate(inflater, container, false);
        model.currentUser().observe(getViewLifecycleOwner(), binding::setUser);
        SwipeRefreshLayout swipeRefreshLayout = binding.refresher;
        swipeRefreshLayout.setOnRefreshListener(model::refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.progress_blue, R.color.progress_red, R.color.progress_green);
        model.isRefreshing().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);
        mRadarView = binding.radarView;
        FloatingActionButton mCreditInfoButton = binding.creditInfoButton;
        mCreditInfoButton.setOnClickListener(v -> new AlertDialog.Builder(getContext()).setTitle("关于信用分")
                .setMessage("消费：用户的消费行为\n守约：用户遵守租约的频率\n支付：用户不拖欠支付租赁费用\n守时：物品定时归还")
                .setPositiveButton("了解", (dialog, which) -> {
                }).show());
        ImageView avatar = binding.avatar;
        Glide.with(avatar)
                .load(Constant.PICTURE_URL + Application.user.getAvatarId())
                .error(R.drawable.error)
                .into(avatar);
        getData();
        model.refresh();
        return binding.getRoot();
    }

    private void getData() {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("userId", "0");
        Call<Credit> creditCall = Constant.CREDIT_SERVICE.getDimensions(userMap);
        creditCall.enqueue(new Callback<Credit>() {
            @Override
            public void onResponse(@NonNull Call<Credit> call, @NonNull Response<Credit> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                if (response.body() == null) {
                    Log.e(Constant.NAME, "receive null body");
                    return;
                }
                final List<RadarData> radarDataList = response.body().getDimensions();
                if (radarDataList == null || radarDataList.isEmpty()) {
                    return;
                }
                radarDataList.get(1).setPercent(0.7);
                radarDataList.get(2).setPercent(0.6);
                radarDataList.get(3).setPercent(0.8);
                mRadarView.setData(radarDataList, 1);
                mRadarView.setNum(5);
            }

            @Override
            public void onFailure(@NonNull Call<Credit> call, @NonNull Throwable t) {
                Log.e(Constant.NAME, "fetch credit dimension failed");
                Snackbar.make(mRadarView, "接收失败", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
