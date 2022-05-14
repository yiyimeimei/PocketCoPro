package com.memoryleak.pocketcopro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.memoryleak.pocketcopro.R;
import com.memoryleak.pocketcopro.data.DeviceInfo1;

import java.util.List;

public class DeviceDetailAdapter1 extends ArrayAdapter<DeviceInfo1> {
    private int resourceId;

    public DeviceDetailAdapter1(Context context, int textViewResourceId, List<DeviceInfo1> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent){
        DeviceInfo1 model = getItem(position);
        View view;
        if (converView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false );
        }else {
            view = converView;
        }
        TextView titleView = view.findViewById(R.id.device_info_name);
        TextView descView = view.findViewById(R.id.device_info);
        titleView.setText(model.getName());
        descView.setText(model.getValue());
        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(DeviceDetail.class, "OMG宝宝你", Toast.LENGTH_SHORT).show();
            }
        });*/
        return view;
    }
}
