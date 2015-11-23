package com.example.cripz.bluetoothrccarcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Device extends Activity implements OnItemClickListener {

    private ArrayList<HashMap<String, String>> listItems = new ArrayList<>();
    private String DEVICE_NAME = "name";
    private String DEVICE_ADDRESS = "address";
    public final static String EXTRA_DEVICE_ADDRESS = "EXTRA_DEVICE_ADDRESS";
    public final static String EXTRA_DEVICE_NAME = "EXTRA_DEVICE_NAME";

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
        Map<String, String> hashMap = listItems.get(position);

        String addr = hashMap.get(DEVICE_ADDRESS);
        String name = hashMap.get(DEVICE_NAME);
        Intent intent = new Intent(Device.this, MainControl.class);
        intent.putExtra(EXTRA_DEVICE_ADDRESS, addr);
        intent.putExtra(EXTRA_DEVICE_NAME, name);
        startActivity(intent);
        Main.instance.finish(); // destroy the Main Activity
        finish(); // destroy Device Activity
    }
}
