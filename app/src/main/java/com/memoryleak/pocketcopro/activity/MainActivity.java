package com.memoryleak.pocketcopro.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.databinding.ActionMainBinding;
import com.memoryleak.pocketcopro.databinding.ActivityMainBinding;
import com.memoryleak.pocketcopro.databinding.NavigationHeaderBinding;
import com.memoryleak.pocketcopro.model.MainViewModel;
import com.memoryleak.pocketcopro.util.Application;
import com.memoryleak.pocketcopro.util.Constant;

import java.io.File;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private MainViewModel model = null;
    private File detected = null;
    private ActivityResultLauncher<Intent> ocrLauncher = null;
    private EditText editTextInput;
    private ImageView imageViewOutput;
    private AlertDialog.Builder builder;
    private LayoutInflater mLayoutInflater;
    private View view;
    private Integer deviceNumber = 1;
    private String [] deviceName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (model == null) model = new ViewModelProvider(this).get(MainViewModel.class);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = binding.toolbar.toolbar;
        DrawerLayout drawer = binding.drawer;
        NavigationView navigationView = binding.navigation;
        setSupportActionBar(toolbar);
        scanInit();
        binding.container.post(() -> {
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.fragment_commodity, R.id.fragment_order).setOpenableLayout(drawer).build();
            NavController controller = Navigation.findNavController(binding.container);
            NavigationUI.setupWithNavController(toolbar, controller, appBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, controller);
            controller.addOnDestinationChangedListener((navController, destination, arguments) -> {
                if (destination.getId() != R.id.fragment_commodity) {
                    model.actionMenuShown().postValue(false);
                    binding.actions.getRoot().setVisibility(View.GONE);
                } else binding.actions.getRoot().setVisibility(View.VISIBLE);
            });
        });
        NavigationHeaderBinding navigationHeaderBinding = NavigationHeaderBinding.inflate(getLayoutInflater());
        ImageView avatar = navigationHeaderBinding.avatar;
        Glide.with(avatar)
                .load(Constant.PICTURE_URL + Application.user.getAvatarId())
                .error(R.drawable.error)
                .into(avatar);
        avatar.setOnClickListener(v -> {
            drawer.close();
            Navigation.findNavController(binding.container).navigate(R.id.fragment_user);
        });
        navigationView.addHeaderView(navigationHeaderBinding.getRoot());
        detected = new File(getCacheDir(), "detect.jpg");
        ocrLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Log.i(Constant.NAME, "a image to detect");
                GeneralBasicParams param = new GeneralParams();
                param.setDetectDirection(false);
                param.setImageFile(detected);
                OCR.getInstance(getApplicationContext()).recognizeWebimage(param, new OnResultListener<GeneralResult>() {
                    @Override
                    public void onResult(GeneralResult result) {
                        String query = result.getWordList().stream().map(WordSimple::getWords).reduce("", (s1, s2) -> s1.equals("") ? s2 : s1 + ' ' + s2);
                        Log.i(Constant.NAME, "ocr successful, result: " + query);
                        model.updateSearch(query);
                    }

                    @Override
                    public void onError(OCRError error) {
                        Log.e(Constant.NAME, "ocr fail", error);
                    }
                });
            }
        });
        setupFloatingActionButton(binding.actions);
        if (!setupLocationService()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle(R.string.location_fail_title)
                    .setMessage(R.string.location_fail_message)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> finish())
                    .create()
                    .show();
        }
    }

    private void setupFloatingActionButton(ActionMainBinding binding) {
        ExtendedFloatingActionButton explore = binding.explore;
        explore.setOnClickListener(v -> {
            MutableLiveData<Boolean> actionMenuShown = model.actionMenuShown();
            actionMenuShown.postValue(!Objects.requireNonNull(actionMenuShown.getValue()));
        });
        model.actionMenuShown().observe(this, shown -> {
            if (shown) {
                binding.setVisibility(View.VISIBLE);
                explore.extend();
                /*binding.search.show();
                binding.detect.show();*/
                binding.filter.show();
                binding.nearby.show();
            } else {
                binding.setVisibility(View.INVISIBLE);
                explore.shrink();
                /*binding.search.hide();
                binding.detect.hide();*/
                binding.filter.hide();
                binding.nearby.hide();
            }
        });
        /*binding.search.setOnClickListener(v -> {
            SearchView searchView = findViewById(R.id.search);
            searchView.setIconified(false);
            model.actionMenuShown().postValue(false);
        });
        binding.detect.setOnClickListener(v -> {
            Log.i(Constant.NAME, "begin ocr");
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, detected.getAbsolutePath());
            intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL);
            ocrLauncher.launch(intent);
        });*/
        binding.filter.setOnClickListener(v -> {
            Log.i(Constant.NAME, "scan qrcode");
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            intentIntegrator.setPrompt("扫描设备二维码获取设备信息");
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.initiateScan();
        });
        binding.nearby.setOnClickListener(v -> Navigation.findNavController(this, R.id.container).navigate(R.id.action_nearby));//切换fragment至nearby
    }

    private boolean setupLocationService() {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manager == null) {
            Log.e(Constant.NAME, "no location manager found");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(Constant.NAME, "missing location permission");
            return false;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        String provider = manager.getBestProvider(criteria, true);
        if (provider != null) {
            model.location().postValue(manager.getLastKnownLocation(provider));
            manager.requestLocationUpdates(provider, Constant.MINIMUM_LOCATION_UPDATE_TIME, Constant.MINIMUM_LOCATION_UPDATE_DISTANCE, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    model.location().postValue(location);
                    Log.i(Constant.NAME, "location changed to " + location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.w(Constant.NAME, "status of location provider " + provider + " change to " + status + ", extra information: " + extras);
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.i(Constant.NAME, "location provider " + provider + " enabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.i(Constant.NAME, "location provider " + provider + " disabled");
                }
            });
        } else {
            Log.w(Constant.NAME, "no location provider match requirement");
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    //二维码扫描
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "你终止了扫描二维码", Toast.LENGTH_SHORT).show();
            } else {
                String name = result.getContents();
                int dv = findDevice(name);
                if(dv == 0)
                    Toast.makeText(MainActivity.this, "请扫描正确的二维码！", Toast.LENGTH_SHORT).show();
                else
                {
                    String itemName = "设备名称：" + name;
                    String itemStatus = "设备状态：正常";
                    deviceNumber = dv;
                    String[] items = new String[] {itemName, itemStatus};
                    builder.setItems(items, new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, items[i], Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void scanInit() {
        deviceName = this.getResources().getStringArray(R.array.device_name);
        builder = new AlertDialog.Builder(this);
        mLayoutInflater = LayoutInflater.from(this);
        view=mLayoutInflater.inflate(R.layout.alert_dialog, null);

        builder.setTitle("设备信息");
        builder.setPositiveButton("查看详情", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent detailPage = new Intent(MainActivity.this , DeviceDetailActivity.class);
                detailPage.putExtra("deviceNumber", String.valueOf(deviceNumber));
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

    private int findDevice(String str)
    {
        int len = deviceName.length;
        for(int i = 0; i < len; ++i)
        {
            if(str.equals(deviceName[i]))
                return (i + 1);
        }
        return 0;
    }
}
