package com.example.cripz.bluetoothrccarcontroller;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by porsh on 12/19/2015.
 */
public class Accelerometer extends AppCompatActivity implements SensorEventListener {
    private float x, y, z;
    private TextView stateText;
    private String state = " ";
    private String lastState = "  ";
    private float x0, y0, z0;
    private Sensor acc;
    private SensorManager senMng;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer_main);

        senMng = (SensorManager)getSystemService(SENSOR_SERVICE);
        acc = senMng.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(acc != null){
            senMng.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);

            Toast.makeText(getApplicationContext(), "Success!",Toast.LENGTH_LONG ).show();
        }else{
            Toast.makeText(getApplicationContext(), "Your device do not have an accelerometer",Toast.LENGTH_LONG ).show();
        }


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = Math.round(event.values[0]);
        y = Math.round(event.values[1]);
        z = Math.round(event.values[2]);

        //if(flag == 0) {
            //x0Text = events.values[0];
            x0 = 10;
            y0 = 0;
            z0 = 0;
           // flag = 1;
        //}

        System.out.println("X: " + x + "   Y: " + y + "   Z: " + z);
        stateText = (TextView)findViewById(R.id.stateTextID);

       // stateText.setText("X: " + x + "   Y: " + y + "   Z: " + z);

        if(z > 1) {
            state = "MOVING_FORWARD";
            stateText.setText(state);

            if (!lastState.equals(state)) {
                MainControl.sendMessage("j");
                MainControl.sendMessage("h");
                MainControl.sendMessage("g");
                MainControl.sendMessage("f");
                lastState = state;
            }


        }else if(z < -1) {
            state = "MOVING_BACKWARD";
            stateText.setText(state);

            if (!lastState.equals(state)) {
                MainControl.sendMessage("k");
                MainControl.sendMessage("j");
                MainControl.sendMessage("h");
                MainControl.sendMessage("b");
                lastState = state;
            }
        }else if(y <= -2){
            state = "MOVING_LEFT";
            stateText.setText(state);

            if(!lastState.equals(state)) {
                MainControl.sendMessage("k");
                MainControl.sendMessage("g");
                MainControl.sendMessage("j");
                MainControl.sendMessage("l");
                lastState = state;
            }
        }else if(y > 2){
            state = "MOVING_RIGHT";
            stateText.setText(state);
            if(!lastState.equals(state)){
                MainControl.sendMessage("k");
                MainControl.sendMessage("g");
                MainControl.sendMessage("h");
                MainControl.sendMessage("r");
                lastState = state;
            }
        } else if((z >= -1 && z <= 1) && (y >-2 && y < 2)) {
            state = "STAY";
            stateText.setText(state);
            if(!lastState.equals(state)) {
                MainControl.sendMessage("k");
                MainControl.sendMessage("g");
                MainControl.sendMessage("h");
                MainControl.sendMessage("j");
                lastState = state;
            }
        }
    }

    protected void onStop() {
        MainControl.sendMessage("k");
        MainControl.sendMessage("g");
        MainControl.sendMessage("h");
        MainControl.sendMessage("j");
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use
    }
}
