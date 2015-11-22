package com.example.cripz.bluetoothrccarcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Device extends Activity implements OnItemClickListener {

    private ArrayList<Map<String, String>> listItems = new ArrayList<>();
    private String DEVICE_NAME = "name";
    private String DEVICE_ADDRESS = "address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);

        ListView myListView = (ListView) findViewById(R.id.deviceListView);

        for (BluetoothDevice device : Main.mDevices) {
            HashMap<String, String> map = new HashMap<>();
            map.put(DEVICE_NAME, device.getName());
            map.put(DEVICE_ADDRESS, device.getAddress());
            listItems.add(map);
        }

        if(listItems.isEmpty()){
            setTitle("No BLE devices found");
        }
        else {
            setTitle("Device");
        }
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), listItems,
                R.layout.list_item, new String[]{"name", "address"},
                new int[]{R.id.deviceName, R.id.deviceAddr});
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        HashMap<String, String> hashMap = (HashMap<String, String>) listItems
                .get(position);
    }
}
