package com.example.cripz.bluetoothrccarcontroller;

import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnTouchListener;

public class MainControl extends Activity {

    private String mDeviceName;
    private String mDeviceAddress;
    private RBLService mBluetoothLeService;
    private HashMap<UUID, BluetoothGattCharacteristic> map = new HashMap<>();

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((RBLService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                getGattService(mBluetoothLeService.getSupportedGattService());
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                //coming soon
            }
        }
    };
    private void sendMessage(String arg) {
        BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
        characteristic.setValue(arg);
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    private void buttonInitialization(){
        Button forward_button = (Button) findViewById(R.id.forward_btn);
        Button backward_button = (Button) findViewById(R.id.backward_btn);
        Button left_button = (Button) findViewById(R.id.left_btn);
        Button right_button = (Button) findViewById(R.id.right_btn);

        MyTouchListener touchListener = new MyTouchListener();
        forward_button.setOnTouchListener(touchListener);
        backward_button.setOnTouchListener(touchListener);
        left_button.setOnTouchListener(touchListener);
        right_button.setOnTouchListener(touchListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_main);
        buttonInitialization();

        Intent intent = getIntent();

        mDeviceAddress = intent.getStringExtra(Device.EXTRA_DEVICE_ADDRESS);
        mDeviceName = intent.getStringExtra(Device.EXTRA_DEVICE_NAME);

        Intent gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();

            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetoothLeService.disconnect();
        mBluetoothLeService.close();

        System.exit(0);
    }

    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;

        BluetoothGattCharacteristic characteristic = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
        map.put(characteristic.getUuid(), characteristic);

        BluetoothGattCharacteristic characteristicRx = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
        mBluetoothLeService.setCharacteristicNotification(characteristicRx,
                true);
        mBluetoothLeService.readCharacteristic(characteristicRx);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    public class MyTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(v.getId()){
                case R.id.forward_btn:
                    //forward button is called
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendMessage("f");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendMessage("k");
                    }
                    break;

                case R.id.right_btn:
                    //right button is called
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendMessage("r");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendMessage("j");
                    }
                    break;

                case R.id.left_btn:
                    //left button is called
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendMessage("l");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendMessage("h");
                    }
                    break;

                case R.id.backward_btn:
                    //backward button is called
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendMessage("b");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendMessage("g");
                    }
                    break;
            }
            return true;
        }
    }
}
