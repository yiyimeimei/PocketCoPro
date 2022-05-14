package com.memoryleak.pocketcopro.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.activity.DeviceDetailActivity;
import com.memoryleak.pocketcopro.activity.MainActivity;
import com.memoryleak.pocketcopro.data.CommodityItem;
import com.memoryleak.pocketcopro.data.Order;
import com.memoryleak.pocketcopro.data.Point;
import com.memoryleak.pocketcopro.databinding.NearbyItemBinding;
import com.memoryleak.pocketcopro.model.MainViewModel;
import com.memoryleak.pocketcopro.util.Application;
import com.memoryleak.pocketcopro.util.Constant;
import com.memoryleak.pocketcopro.util.Function;
import com.memoryleak.pocketcopro.util.RestCallback;
import com.memoryleak.pocketcopro.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyFragment extends ArFragment implements SensorEventListener {
    private static final double radius = 1.0;
    private MainViewModel mainViewModel = null;
    private Node root = null;
    private List<CommodityItem> items = new ArrayList<>();
    private final float[] magnetometer = new float[3];
    private final float[] accelerometer = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];
    private AlertDialog.Builder builder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainViewModel == null)
            mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        setExitTransition(new MaterialElevationScale(false));
        setReenterTransition(new MaterialElevationScale(true));
        init();
        setOnSessionInitializationListener(session -> {
            Log.i(Constant.NAME, "---------------ar session initialized");
            session.getConfig().setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
            getArSceneView().getScene().getCamera().setFarClipPlane(1000.0f);
            setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                Log.i(Constant.NAME, "--------------ar plane tapped");
                if (root != null) return;
                if (items == null) {
                    Toast.makeText(requireContext(), R.string.preparing, Toast.LENGTH_SHORT).show();
                    return;
                }
                Vector3 cameraPosition = getArSceneView().getScene().getCamera().getWorldPosition();
                Pose pose = Pose.makeTranslation(cameraPosition.x, cameraPosition.y, cameraPosition.z);
                Anchor anchor = session.createAnchor(pose.extractTranslation());
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(getArSceneView().getScene());
                this.root = anchorNode;
                place(mainViewModel.location().getValue());
            });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainViewModel.location().observe(getViewLifecycleOwner(), location -> {
            if (location == null) return;
            /*Map<String, Object> request = new HashMap<>();
            request.put(Constant.LATITUDE, location.getLatitude());
            request.put(Constant.LONGITUDE, location.getLongitude());
            request.put(Constant.DISTANCE, radius);*/

            //Point p1 = new Point(31.0233816, 121.4315885);
            //Point p2 = new Point(31.023378, 121.431583);
            //CommodityItem temp1 = new CommodityItem("device1", "1", p1, 1.0, 1.0);
            //items.add(temp1);
            Double x = location.getLatitude();
            Double y = location.getLongitude();
            Point p1 = new Point(y, x + 0.1);
            CommodityItem temp1 = new CommodityItem("device3", "1", p1, 0.0, 0.001);
            items.add(temp1);
            Point p2 = new Point(y, x - 0.1);
            CommodityItem temp2 = new CommodityItem("device3", "2", p2, 0.0, 0.001);
            items.add(temp2);
            place(location);

            //Log.i(Constant.NAME, "find items within " + radius + ": " + items);
            /*Constant.COMMODITY_NETWORK_SERVICE.findWithin(request).enqueue((RestCallback<List<CommodityItem>>) (data, e) -> {
                if (e != null) {
                    Log.e(Constant.NAME, "find items within " + radius + " fail", e);
                    return;
                }
                Log.i(Constant.NAME, "find items within " + radius + ": " + items);
                this.items = data;
                place(location);
            });*/
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) return;
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Sensor accelerateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) return;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) return;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            System.arraycopy(event.values, 0, magnetometer, 0, magnetometer.length);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            System.arraycopy(event.values, 0, accelerometer, 0, accelerometer.length);
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer, magnetometer);
        SensorManager.getOrientation(rotationMatrix, orientation);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void place(Location current) {
        if (current == null || root == null || items == null) return;
        Log.d("--------------当前坐标纬度----------", String.valueOf(current.getLatitude()));
        Log.d("--------------当前坐标经度----------", String.valueOf(current.getLongitude()));
        Point currentPoint = new Point(current.getLatitude(), current.getLongitude());
        Vector3 temp = Utility.transformPosition(current, currentPoint, 90.0, 1.0 * 1000, orientation[0]);
        Log.d("------self position-------", temp.x + " " + temp.y + " " + temp.z);
        items.forEach(item -> {
            Log.d("----item-----", item.getName() +" "+ item.getLocation().getX() + " " + item.getLocation().getY());
            NearbyItemBinding binding = NearbyItemBinding.inflate(getLayoutInflater());
            binding.setItem(item);
            Glide.with(requireContext())
                    .load(R.drawable.ban)
                    .error(R.drawable.error)
                    .into(binding.icon);
            CommodityNode node = new CommodityNode(binding);
            node.setParent(root);
            ViewRenderable.builder().setView(requireContext(), binding.getRoot()).build().thenAccept(node::setRenderable);

            Vector3 position1 = Utility.transformPosition(current, item.getLocation(), item.getHeight(), item.getDistance() * 1000, orientation[0]);
            /*Point tempPoint = item.getLocation();
            double t1 = currentPoint.getX() - tempPoint.getX();
            double t2 = t1 * 111320.111;
            float tx = (float)t2 + temp.x;

            t1 = item.getHeight();
            float ty = (float)t1;

            t1 = currentPoint.getY() - tempPoint.getY();
            t2 = t1 * 111320.111;
            float tz = (float)t2 + temp.z;
            Vector3 position1 = new Vector3(tx, ty, tz);*/
            Log.d(item.getName(), "----------place node at relative position: " + position1);
            node.setLocalPosition(position1);
            View container = binding.container;
            binding.setExpanded(false);
            container.setOnClickListener(v -> {
                boolean expanded = !binding.getExpanded();
                if (expanded) {
                    root.getChildren().forEach(n -> {
                        if (n != node && n instanceof CommodityNode)
                            n.setEnabled(false);
                    });
                } else {
                    root.getChildren().forEach(n -> {
                        if (n instanceof CommodityNode)
                            n.setEnabled(true);
                    });
                }
                binding.setExpanded(expanded);
            });
            /*binding.navigate.setOnClickListener(v -> {
                Log.d("------------------","点击navigate");
                Point point = item.getLocation();
                Uri uri = Uri.parse("geo:" + point.getY() + ',' + point.getX());
                Log.i(Constant.NAME, "navigate to: " + uri.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.navigate_to)));
                NavHostFragment.findNavController(this).popBackStack();
            });*/
            binding.order.setOnClickListener(v -> {
                String itemName = "设备名称：device3";
                String itemStatus = "设备状态：正常";
                String[] items = new String[] {itemName, itemStatus};
                builder.setItems(items, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            });

            /*binding.order.setOnClickListener(v -> Utility.confirmOrder(requireContext(), () -> {
                Toast.makeText(requireContext(), "aaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
                Map<String, Object> request = new HashMap<>();
                request.put(Constant.COMMODITY_ID, item.getCommodityId());
                request.put(Constant.COMMODITY_ITEM_ID, item.getItemId());
                request.put(Constant.USER_ID, Application.user.getUserId());
                Function hook = Utility.loading(requireContext());
                Constant.ORDER_NETWORK_SERVICE.create(request).enqueue((RestCallback<Order>) (order, e) -> {
                    hook.apply();
                    if (e != null) {
                        Log.e(Constant.NAME, "create order fail", e);
                        Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mainViewModel.selectOrder(order);
                    NavController controller = NavHostFragment.findNavController(this);
                    controller.navigate(R.id.action_place_order);
                });
            }));*/
        });
    }

    /*private void place(Location current) {
        if (current == null || root == null || items == null) return;
        items.forEach(item -> {
            Log.i(Constant.NAME, "create node ...");
            NearbyItemBinding binding = NearbyItemBinding.inflate(getLayoutInflater());
            binding.setItem(item);
            Glide.with(requireContext())
                    .load(Constant.PICTURE_URL + item.getIconId())
                    .error(R.drawable.error)
                    .into(binding.icon);
            CommodityNode node = new CommodityNode(binding);
            node.setParent(root);
            ViewRenderable.builder().setView(requireContext(), binding.getRoot()).build().thenAccept(node::setRenderable);
            Vector3 position = Utility.transformPosition(current, item.getLocation(), item.getHeight(), item.getDistance() * 1000, orientation[0]);
            Log.d(Constant.NAME, "place node at relative position: " + position);
            node.setLocalPosition(position);
            View container = binding.container;
            binding.setExpanded(false);
            container.setOnClickListener(v -> {
                boolean expanded = !binding.getExpanded();
                if (expanded) {
                    root.getChildren().forEach(n -> {
                        if (n != node && n instanceof CommodityNode)
                            n.setEnabled(false);
                    });
                } else {
                    root.getChildren().forEach(n -> {
                        if (n instanceof CommodityNode)
                            n.setEnabled(true);
                    });
                }
                binding.setExpanded(expanded);
            });
            binding.navigate.setOnClickListener(v -> {
                Point point = item.getLocation();
                Uri uri = Uri.parse("geo:" + point.getY() + ',' + point.getX());
                Log.i(Constant.NAME, "navigate to: " + uri.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.navigate_to)));
                NavHostFragment.findNavController(this).popBackStack();
            });
            binding.order.setOnClickListener(v -> Utility.confirmOrder(requireContext(), () -> {
                Map<String, Object> request = new HashMap<>();
                request.put(Constant.COMMODITY_ID, item.getCommodityId());
                request.put(Constant.COMMODITY_ITEM_ID, item.getItemId());
                request.put(Constant.USER_ID, Application.user.getUserId());
                Function hook = Utility.loading(requireContext());
                Constant.ORDER_NETWORK_SERVICE.create(request).enqueue((RestCallback<Order>) (order, e) -> {
                    hook.apply();
                    if (e != null) {
                        Log.e(Constant.NAME, "create order fail", e);
                        Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mainViewModel.selectOrder(order);
                    NavController controller = NavHostFragment.findNavController(this);
                    controller.navigate(R.id.action_place_order);
                });
            }));
        });
    }*/

    private static class CommodityNode extends Node {
        private final NearbyItemBinding binding;

        public CommodityNode(NearbyItemBinding binding) {
            this.binding = binding;
        }

        @Override
        public void onUpdate(FrameTime frameTime) {
            super.onUpdate(frameTime);
            assert getScene() != null;
            Vector3 cameraPosition = getScene().getCamera().getWorldPosition();
            Vector3 selfPosition = getWorldPosition();
            Vector3 direction = Vector3.subtract(cameraPosition, selfPosition);
            Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
            binding.setHumanizedDistance(Utility.humanizeDistance(direction.length() / 1000.0));
            setWorldRotation(lookRotation);
            setLocalScale(Utility.adjustScale(direction.length(), getLocalScale()));
        }
    }

    private void init()
    {
        builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("设备信息");
        builder.setPositiveButton("查看详情", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent detailPage = new Intent(requireContext() , DeviceDetailActivity.class);
                detailPage.putExtra("deviceNumber", String.valueOf(3));
                startActivity(detailPage);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

}
