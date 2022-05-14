package com.memoryleak.pocketcopro.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.adapter.DeviceDetailAdapter1;
import com.memoryleak.pocketcopro.data.DeviceInfo1;
import com.memoryleak.pocketcopro.util.Function;

import java.util.ArrayList;
import java.util.List;

public class DeviceDetailActivity extends AppCompatActivity {
    private ListView deviceList1;
    private DeviceDetailAdapter1 deviceDetailAdapter1;
    private Button btn_feedback;
    private AlertDialog dialog;
    List<DeviceInfo1> infoData = new ArrayList<DeviceInfo1>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list_view);
        deviceList1 = findViewById(R.id.device_list1);
        btn_feedback = findViewById(R.id.btn_feedback);
        init();
        Intent intent = getIntent();
        Integer deviceNumber =  Integer.parseInt(intent.getStringExtra("deviceNumber"));
        switch (deviceNumber)
        {
            case 1:
            {
                infoData.add(new DeviceInfo1("设备信息：", "182138711"));
                infoData.add(new DeviceInfo1("设备名称：", "device1"));
                infoData.add(new DeviceInfo1("设备状态：", "正常"));
                infoData.add(new DeviceInfo1("设备位置：", "XX大学"));
                infoData.add(new DeviceInfo1("设备利用率：", "90%"));
                infoData.add(new DeviceInfo1("生产任务：", "汽车制造"));
                break;
            }
            case 2:
            {
                infoData.add(new DeviceInfo1("设备信息：", "114514114"));
                infoData.add(new DeviceInfo1("设备名称：", "device2"));
                infoData.add(new DeviceInfo1("设备状态：", "正常"));
                infoData.add(new DeviceInfo1("设备位置：", "XX大学"));
                infoData.add(new DeviceInfo1("设备利用率：", "70%"));
                infoData.add(new DeviceInfo1("生产任务：", "汽车制造"));
                break;
            }
            case 3:
            {
                infoData.add(new DeviceInfo1("设备信息：", "147285693"));
                infoData.add(new DeviceInfo1("设备名称：", "device3"));
                infoData.add(new DeviceInfo1("设备状态：", "正常"));
                infoData.add(new DeviceInfo1("设备位置：", "XX大学"));
                infoData.add(new DeviceInfo1("设备利用率：", "97%"));
                infoData.add(new DeviceInfo1("生产任务：", "汽车制造"));
                break;
            }
            case 4:
            {
                infoData.add(new DeviceInfo1("设备信息：", "147285693"));
                infoData.add(new DeviceInfo1("设备名称：", "device3"));
                infoData.add(new DeviceInfo1("设备状态：", "异常"));
                infoData.add(new DeviceInfo1("设备位置：", "XX大学"));
                infoData.add(new DeviceInfo1("设备利用率：", "0%"));
                infoData.add(new DeviceInfo1("生产任务：", "汽车制造"));
                break;
            }
        }
        deviceDetailAdapter1 = new DeviceDetailAdapter1(this, R.layout.list_view_item1, infoData);
        deviceList1.setAdapter(deviceDetailAdapter1);


    }
    private void init()
    {
        dialog = new AlertDialog.Builder(this)
                .setTitle("反馈设备异常")
                .setMessage("是否确认要上报？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(DeviceDetailActivity.this, "已成功反馈", Toast.LENGTH_SHORT).show();
                        btn_feedback.setEnabled(false);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        btn_feedback.setOnClickListener(v -> {
            dialog.show();
        });
    }

}