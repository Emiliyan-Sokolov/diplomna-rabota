package com.example.cripz.bluetoothrccarcontroller;

import java.io.UnsupportedEncodingException;
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
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class MainControl extends Activity {

    private ImageButton forwardButton;
    private ImageButton reverseButton;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private Boolean carLightsFlag = false;
    private String mDeviceName;
    private String mDeviceAddress;
    private static RBLService mBluetoothLeService;
    private static MainControl mainControlInstance = null;
    private static HashMap<UUID, BluetoothGattCharacteristic> map = new HashMap<>();

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

    private void displayData(byte[] data) {

        String bytesAsString = null;
        try {
            bytesAsString = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("test", bytesAsString);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(mainControlInstance);
                builder.title("The device has been disconnected.");
                builder.neutralText("Exit");
                builder.onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                });
                builder.cancelable(false);
                builder.show();
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                getGattService(mBluetoothLeService.getSupportedGattService());
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
            }
        }
    };

    public static void sendMessage(String arg) {
        BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
        characteristic.setValue(arg);
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    private void initializeButtonVariables(){
        forwardButton = (ImageButton) findViewById(R.id.forward_btn);
        reverseButton = (ImageButton) findViewById(R.id.reverse_btn);
        leftButton = (ImageButton) findViewById(R.id.left_btn);
        rightButton = (ImageButton) findViewById(R.id.right_btn);
        Button carControl = (Button) findViewById(R.id.car_control);
        Button carLights = (Button) findViewById(R.id.car_lights);
        MyTouchListener touchListener = new MyTouchListener();
        forwardButton.setOnTouchListener(touchListener);
        reverseButton.setOnTouchListener(touchListener);
        leftButton.setOnTouchListener(touchListener);
        rightButton.setOnTouchListener(touchListener);
        carControl.setOnTouchListener(touchListener);
        carLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carLightsFlag) {
                    sendMessage("v");
                    carLightsFlag = false;
                }else {
                    sendMessage("n");
                    carLightsFlag = true;
                }
            }
        });
        carLights.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                sendMessage("m");
                return true;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_main);
        initializeButtonVariables();
        mainControlInstance = this;

        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(Main.EXTRA_DEVICE_ADDRESS);
        mDeviceName = intent.getStringExtra(Main.EXTRA_DEVICE_NAME);

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
        sendMessage("k");
        sendMessage("g");
        sendMessage("j");
        sendMessage("h");
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
        public boolean onTouch(View v, MotionEvent event) {switch(v.getId()){
                case R.id.forward_btn:
                    //forward button is called
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        forwardButton.setImageResource(R.drawable.btn_forward_clicked);
                        sendMessage("f");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        forwardButton.setImageResource(R.drawable.btn_forward);
                        sendMessage("k");
                    }
                    break;

                case R.id.right_btn:
                    //right button is called
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        rightButton.setImageResource(R.drawable.btn_right_clicked);
                        sendMessage("r");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        rightButton.setImageResource(R.drawable.btn_right);
                        sendMessage("j");
                    }
                    break;

                case R.id.left_btn:
                    //left button is called
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        leftButton.setImageResource(R.drawable.btn_left_clicked);
                        sendMessage("l");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        leftButton.setImageResource(R.drawable.btn_left);
                        sendMessage("h");
                    }
                    break;

                case R.id.reverse_btn:
                    //backward button is called
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        reverseButton.setImageResource(R.drawable.btn_reverse_clicked);
                        sendMessage("b");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        reverseButton.setImageResource(R.drawable.btn_reverse);
                        sendMessage("g");
                    }
                    break;
                    
                 case R.id.car_control:
                    //accelerometer called
                    Intent accelerometer_control = new Intent(MainControl.this,Accelerometer.class);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startActivity(accelerometer_control);

                    }/* else if (event.getAction() == MotionEvent.ACTION_UP) {

                    }*/
                    break;
            }
            return true;
        }
    }
}
