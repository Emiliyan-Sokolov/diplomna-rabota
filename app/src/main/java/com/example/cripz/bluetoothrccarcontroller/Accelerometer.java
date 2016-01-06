package com.example.cripz.bluetoothrccarcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.Toast;


public class Accelerometer extends AppCompatActivity implements SensorEventListener {
    private float x, y, z;
    private TextView stateText;
    private String state = "state";
    private String lastState = "lastState";
    // private float x0, y0, z0;
    private Sensor acc;
    private SensorManager senMng;


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

    private String goForward(){
        MainControl.sendMessage("f"); //go forward
        MainControl.sendMessage("g"); //stop backward
        MainControl.sendMessage("h"); //stop left
        MainControl.sendMessage("j"); //stop right
        lastState = "MOVING_FORWARD";

        return lastState;
    }

    private String goBackward(){
        MainControl.sendMessage("k"); //stop forward
        MainControl.sendMessage("b"); //go backward
        MainControl.sendMessage("h"); //stop left
        MainControl.sendMessage("j"); //stop right
        lastState = "MOVING_BACKWARD";

        return lastState;
    }

    private String goRight(){
        MainControl.sendMessage("k"); //stop forward
        MainControl.sendMessage("g"); //stop backward
        MainControl.sendMessage("h"); //stop left
        MainControl.sendMessage("r"); //go right
        lastState = "MOVING_RIGHT";

        return lastState;
    }

    private String goLeft(){
        MainControl.sendMessage("k"); //stop forward
        MainControl.sendMessage("g"); //stop backward
        MainControl.sendMessage("l"); //go left
        MainControl.sendMessage("j"); //stop right
        lastState = "MOVING_LEFT";

        return lastState;
    }


    private String stop(){
        MainControl.sendMessage("k"); //stop forward
        MainControl.sendMessage("g"); //stop backward
        MainControl.sendMessage("h"); //stop left
        MainControl.sendMessage("j"); //stop right
        lastState = "STAY";

        return lastState;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = Math.round(event.values[0]);
        y = Math.round(event.values[1]);
        z = Math.round(event.values[2]);

        System.out.println("X: " + x + "   Y: " + y + "   Z: " + z);
        stateText = (TextView)findViewById(R.id.stateTextID);
        //stateText.setText(("X: " + x + "   Y: " + y + "   Z: " + z + state));


        if (y <= -3) {
            state = "MOVING_LEFT";
            if(!lastState.equals(state)){
                goLeft();
            }
            stateText.setText(state);
        }

        if (y >= 3) {
            state = "MOVING_RIGHT";
            if(!lastState.equals(state)){
                goRight();
            }
            stateText.setText(state);
        }

        if(z >= 2){
            state = "MOVING_FORWARD";
            if(!lastState.equals(state)){
                goForward();
                stateText.setText(state);
            }
        }

        if (z <= -2){
            state = "MOVING_BACKWARD";
            if(!lastState.equals(state)){
                goBackward();
            }
            stateText.setText(state);
        }


        if ((y == -1 || y == 0 || y == 1 ) && (z == -1 || z == 0 || z == 1 )) {
            state = "STAY";
            if(!lastState.equals(state)){
                stop();
            }
            stateText.setText(state);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        MainControl.sendMessage("k");
        MainControl.sendMessage("g");
        MainControl.sendMessage("j");
        MainControl.sendMessage("h");
        MainControl.sendMessage("v");
        senMng.unregisterListener(this, acc);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senMng.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use
    }
}
