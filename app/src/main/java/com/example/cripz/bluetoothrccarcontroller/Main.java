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
    private MaterialDialog mDialog;
    public static ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
    public static Main instance = null;

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

        instance = this;

        // Initializes Bluetooth adapter.
        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        //Initializes ProcessDialog
        buildRoundProcessDialog(instance);

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

    private void searchForAvailableDevices() {
        scanLeDevice();
        mDialog.show();

        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                Intent deviceListIntent = new Intent(getApplicationContext(),
                        Device.class);
                startActivity(deviceListIntent);
                mDialog.dismiss();
            }
        }, SCAN_PERIOD);
    }

    public void buildRoundProcessDialog(Context mContext) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder.content(R.string.progress_dialog);
        builder.progress(true, 0);
        builder.cancelable(false);
        mDialog = builder.build();
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
