package com.memoryleak.pocketcopro.util;

import android.Manifest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.memoryleak.pocketcopro.service.CommodityNetworkService;
import com.memoryleak.pocketcopro.service.CreditService;
import com.memoryleak.pocketcopro.service.OrderNetworkService;
import com.memoryleak.pocketcopro.service.UserService;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Constant {

    public static final String PACKAGE = "com.memoryleak.sharesday";
    public static final String USER_PREFERENCE_NAME = PACKAGE + ".user";
    public static final String NAME = "PocketCoPro";
    public static final String API_URL = "http://202.120.40.86:32768";
    public static final String OCR_API_KEY = "HRUsgBbguoTh8CljDCsIVNEI";
    public static final String OCR_SECRET_KEY = "2NgHFG2CqRG8yMgZXYG0jYkdET2OnG7f";
    public static final String IMAGE_API_KEY = "XDAZcCvE5u2qQiYGRBBwgdmI";
    public static final String IMAGE_SECRET_KEY = "v5Kyta1fDwv7b0fFhyKjgbkkdElttVoM";
    public static final String PICTURE_URL = API_URL + "/picture/get?id=";
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(java.time.Instant.class, new InstantTypeAdapter())
            .create();
    public static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create(GSON))
            .build();
    public static final OrderNetworkService ORDER_NETWORK_SERVICE = RETROFIT.create(OrderNetworkService.class);
    public static final UserService USER_SERVICE = RETROFIT.create(UserService.class);
    public static final CommodityNetworkService COMMODITY_NETWORK_SERVICE = RETROFIT.create(CommodityNetworkService.class);
    public static final CreditService CREDIT_SERVICE = RETROFIT.create(CreditService.class);
    public static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
    };

    public static final double EARTH_A = 6378245.0;
    public static final double EARTH_E = 0.00669342162296594323;

    public static final UUID BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // public static final String BLUETOOTH_ADDRESS = "60:AB:67:64:5C:9D";
    public static final String BLUETOOTH_ADDRESS = "A0:57:E3:95:3F:B6";
    public static final byte LOCK = 0X01;
    public static final byte UNLOCK = 0X02;
    public static final byte ERROR = 0X00;
    public static final byte LOCKED = 0X01;
    public static final byte UNLOCKED = 0X02;

    public static final Integer MINIMUM_LOCATION_UPDATE_TIME = 60 * 1000;
    public static final Float MINIMUM_LOCATION_UPDATE_DISTANCE = 100.0f;

    public static final Double MAXIMUM_DISTANCE_10000 = 10000.0;

    public static final float MAXIMUM_SCALE = 1.0f;
    public static final float MINIMUM_SCALE = 0.5f;

    public static final String ID = "_id";
    public static final String ORDER_ID = "orderId";
    public static final String SEARCH = "search";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String COMMODITY_TYPE = "commodityType";
    public static final String DISTANCE = "distance";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USER_ID = "userId";
    public static final String CREATE_TIME = "createTime";
    public static final String COMMODITY_ID = "commodityId";
    public static final String COMMODITY_ITEM_ID = "commodityItemId";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private Constant() {
    }
}
