package com.memoryleak.pocketcopro.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.google.android.gms.maps.model.LatLng;
import com.google.ar.sceneform.math.Vector3;
import com.google.maps.android.SphericalUtil;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.activity.DeviceDetailActivity;
import com.memoryleak.pocketcopro.activity.MainActivity;
import com.memoryleak.pocketcopro.data.Commodity;
import com.memoryleak.pocketcopro.data.Order;
import com.memoryleak.pocketcopro.data.Point;

import java.io.IOException;
import java.util.Collections;

import retrofit2.Response;

public final class Utility {
    private Utility() {
    }

    public static <T> DataSource<T, T> emptyDataSource() {
        return new ItemKeyedDataSource<T, T>() {
            @Override
            public void loadInitial(@NonNull LoadInitialParams<T> params, @NonNull LoadInitialCallback<T> callback) {
                callback.onResult(Collections.emptyList(), 0, 0);
            }

            @Override
            public void loadAfter(@NonNull LoadParams<T> params, @NonNull LoadCallback<T> callback) {
                callback.onResult(Collections.emptyList());
            }

            @Override
            public void loadBefore(@NonNull LoadParams<T> params, @NonNull LoadCallback<T> callback) {
                callback.onResult(Collections.emptyList());
            }

            @NonNull
            @Override
            public T getKey(@NonNull T item) {
                return item;
            }
        };
    }

    public static <T> T retrieveData(Response<Result<T>> response) {
        if (!response.isSuccessful()) throw new RestException(response.code());
        Result<T> result = response.body();
        if (result == null)
            throw new RestException(RestException.NULL_RESPONSE_MESSAGE, RestException.NULL_RESPONSE);
        Result.StatusCode status = result.getStatusCode();
        if (status != Result.StatusCode.SUCCESS)
            throw new RestException(status.name(), status.ordinal());
        Log.i(Constant.NAME, "retrieve data: " + result.getData());
        return result.getData();
    }

    public static String humanizeDistance(Double distanceInKilometers) {
        if (distanceInKilometers == null) return null;
        //return distanceInKilometers + "M";
        if (distanceInKilometers > 1) return distanceInKilometers.longValue() + "kM";
        else return Double.valueOf(distanceInKilometers * 1000).longValue() + "M";
    }

    public static Vector3 transformPosition(Location current, Point point, Double height, Double distance, float azimuth) {
        LatLng currentLatLng = new LatLng(current.getLatitude(), current.getLongitude());
        LatLng latitudeLng = new LatLng(point.getY(), point.getX());
        Log.d(Constant.NAME, "distance: " + distance);
        double heading = SphericalUtil.computeHeading(currentLatLng, latitudeLng);
        float x = (float) (distance * Math.sin(azimuth + heading));
        float y = 0.0f;
        if (height != null) y = (float) (height - current.getAltitude());
        float z = (float) (distance * Math.cos(azimuth + heading));
        return new Vector3(x, y, z);
    }

    public static Vector3 adjustScale(float distance, Vector3 current) {
        float scale = distance * (Constant.MINIMUM_SCALE + (Constant.MAXIMUM_SCALE - Constant.MINIMUM_SCALE) * (float) Math.exp(-distance));
        return current.normalized().scaled(scale);
    }

    public static Function loading(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.loading)
                .setView(R.layout.loading)
                .setCancelable(false)
                .create();
        dialog.show();
        return dialog::dismiss;
    }

    public static String helpHint(Commodity commodity) {
        if (commodity == null) return "";
        return commodity.getModeName() + ": " + commodity.getModeDescription() + '\n' + commodity.getBillingModeName() + ": " + commodity.getBillingModeDescription();
    }

    public static String helpHint(Order order) {
        if (order == null) return "";
        return order.getModeName() + ": " + order.getModeDescription() + '\n' + order.getBillingModeName() + ": " + order.getBillingModeDescription();
    }

    public static void confirmOrder(Context context, Function confirmed) {
        confirmOrder(context, null, confirmed);
    }

    public static void confirmOrder(Context context, String helpHint, Function confirmed) {
        String message = context.getString(R.string.confirm_detail);
        if (helpHint != null) message += '\n' + helpHint;
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.placing_order)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, (d, which) -> confirmed.apply())
                .setNegativeButton(R.string.cancel, (d, which) -> {
                })
                .create();
        dialog.show();
    }

    public static BluetoothSocket connectBluetooth(String address) throws IOException {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            Log.e(Constant.NAME, "bluetooth not supported or disabled");
            return null;
        }
        BluetoothDevice device = adapter.getRemoteDevice(address);
        Log.i(Constant.NAME, "bluetooth device found: " + device);
        return device.createInsecureRfcommSocketToServiceRecord(Constant.BLUETOOTH_UUID);
    }

    public static Point convertWGS84toGCJ02(Point point) {
        double longitude = point.getX();
        double latitude = point.getY();
        double dLatitude = transformLatitude(longitude - 105.0, latitude - 35.0);
        double dLongitude = transformLongitude(longitude - 105.0, latitude - 35.0);
        double radLatitude = latitude / 180.0 * Math.PI;
        double magic = Math.sin(radLatitude);
        magic = 1 - Constant.EARTH_E * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLatitude = (dLatitude * 180.0) / ((Constant.EARTH_A * (1 - Constant.EARTH_E)) / (magic * sqrtMagic) * Math.PI);
        dLongitude = (dLongitude * 180.0) / (Constant.EARTH_A / sqrtMagic * Math.cos(radLatitude) * Math.PI);
        double mgLatitude = latitude + dLatitude;
        double mgLongitude = longitude + dLongitude;
        return new Point(mgLongitude, mgLatitude);
    }

    public static Point convertGCJ02toWGS84(Point point) {
        double longitude = point.getX();
        double latitude = point.getY();
        double dLatitude = transformLatitude(longitude - 105.0, latitude - 35.0);
        double dLongitude = transformLongitude(longitude - 105.0, latitude - 35.0);
        double radLatitude = latitude / 180.0 * Math.PI;
        double magic = Math.sin(radLatitude);
        magic = 1 - Constant.EARTH_E * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLatitude = (dLatitude * 180.0) / ((Constant.EARTH_A * (1 - Constant.EARTH_E)) / (magic * sqrtMagic) * Math.PI);
        dLongitude = (dLongitude * 180.0) / (Constant.EARTH_A / sqrtMagic * Math.cos(radLatitude) * Math.PI);
        double mgLatitude = latitude + dLatitude;
        double mgLongitude = longitude + dLongitude;
        return new Point(longitude * 2 - mgLongitude, latitude * 2 - mgLatitude);
    }

    public static double transformLongitude(double longitude, double latitude) {
        double result = 300.0 + longitude + 2.0 * latitude + 0.1 * longitude * longitude + 0.1 * longitude * latitude + 0.1 * Math.sqrt(Math.abs(longitude));
        result += (20.0 * Math.sin(6.0 * longitude * Math.PI) + 20.0 * Math.sin(2.0 * longitude * Math.PI)) * 2.0 / 3.0;
        result += (20.0 * Math.sin(longitude * Math.PI) + 40.0 * Math.sin(longitude / 3.0 * Math.PI)) * 2.0 / 3.0;
        result += (150.0 * Math.sin(longitude / 12.0 * Math.PI) + 300.0 * Math.sin(longitude / 30.0 * Math.PI)) * 2.0 / 3.0;
        return result;
    }

    public static double transformLatitude(double longitude, double latitude) {
        double result = -100.0 + 2.0 * longitude + 3.0 * latitude + 0.2 * latitude * latitude + 0.1 * longitude * latitude + 0.2 * Math.sqrt(Math.abs(longitude));
        result += (20.0 * Math.sin(6.0 * longitude * Math.PI) + 20.0 * Math.sin(2.0 * longitude * Math.PI)) * 2.0 / 3.0;
        result += (20.0 * Math.sin(latitude * Math.PI) + 40.0 * Math.sin(latitude / 3.0 * Math.PI)) * 2.0 / 3.0;
        result += (160.0 * Math.sin(latitude / 12.0 * Math.PI) + 320 * Math.sin(latitude * Math.PI / 30.0)) * 2.0 / 3.0;
        return result;
    }
}
