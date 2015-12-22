package com.example.cripz.bluetoothrccarcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class Main extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 3000;
    private MaterialDialog processDialog;
    public  ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
    public static Main mainInstance = null;
    public final static String EXTRA_DEVICE_NAME = "EXTRA_DEVICE_NAME";
    public final static String EXTRA_DEVICE_ADDRESS = "EXTRA_DEVICE_ADDRESS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //checks if the device supports BLE
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_LONG).show();
            finish();
        }

        mainInstance = this;

        // Initializes Bluetooth adapter.
        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        //Initializes ProcessDialog
        buildRoundProcessDialog(mainInstance);

        // Checks if Bluetooth is turned on. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            searchForAvailableDevices();
        }

        Button btn = (Button)findViewById(R.id.main_btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForAvailableDevices();
            }
        });
    }

    public void buildRoundProcessDialog(Context mContext) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder.content(R.string.progress_dialog);
        builder.progress(true, 0);
        builder.cancelable(false);
        processDialog = builder.build();
    }

    private void searchForAvailableDevices() {
        scanLeDevice();
        processDialog.show();

        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                showAvailableDevices();
            }
        }, SCAN_PERIOD);
    }

    private void showAvailableDevices() {

        final MaterialDialog.Builder builder = new MaterialDialog.Builder(mainInstance);
        if(mDevices.size() > 0) {
            final String[] devicesName = new String[mDevices.size()];
            final String[] devicesAddr = new String[mDevices.size()];

            for(int i = 0; i < mDevices.size();i++){
                devicesName[i] = mDevices.get(i).getName();
                devicesAddr[i] = mDevices.get(i).getAddress();
            }
            builder.title("Available Devices");
            builder.items(devicesName);
            builder.itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    String name = devicesName[which];
                    String addr = devicesAddr[which];
                    Intent intent = new Intent(Main.this,MainControl.class);
                    intent.putExtra(EXTRA_DEVICE_NAME, name);
                    intent.putExtra(EXTRA_DEVICE_ADDRESS, addr);
                    startActivity(intent);
                }
            });
        } else {
            builder.title("No available devices found.");
            builder.cancelable(true);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processDialog.dismiss();
                builder.show();
            }
        });
    }

    private void scanLeDevice() {
        new Thread() {

            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                try {
                    Thread.sleep(SCAN_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }.start();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null) {
                        // if the device is not in the array
                        if (mDevices.indexOf(device) == -1)
                            mDevices.add(device);
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If user chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
        searchForAvailableDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.exit(0);
    }
}
