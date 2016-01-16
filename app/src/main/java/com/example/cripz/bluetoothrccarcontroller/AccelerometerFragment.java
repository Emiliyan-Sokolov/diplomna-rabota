package com.example.cripz.bluetoothrccarcontroller;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AccelerometerFragment extends Fragment implements SensorEventListener {
    private View rootView;
    private float x, y, z;
    private float x0, y0, z0;
    private TextView stateText;
    private String state = "state";
    private String lastState = "lastState";
    private Sensor acc;
    private SensorManager senMng;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.accelerometer_fragment,container,false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        senMng = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        acc = senMng.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(acc != null){
            senMng.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);

            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "Your device do not have an accelerometer",Toast.LENGTH_LONG ).show();
        }
    }

    private void stop(){
        Main.sendMessage("k"); //stop forward
        Main.sendMessage("g"); //stop backward
        Main.sendMessage("h"); //stop left
        Main.sendMessage("j"); //stop right
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
       /* if(flag == 0) {
            x0 = Math.round(event.values[0]);
            y0 = Math.round(event.values[1]);
            z0 = Math.round(event.values[2]);
            flag = 1;
        }*/

        x = Math.round(event.values[0]);
        y = Math.round(event.values[1]);
        z = Math.round(event.values[2]);

        System.out.println("X: " + x + "   Y: " + y + "   Z: " + z);
        stateText = (TextView)rootView.findViewById(R.id.stateTextID);

        if(z > 2){

            if(y <= -1){

                state = "MOVING_FORWARD_LEFT";
                if(!lastState.equals(state)){
                    stateText.setText(state);
                    Main.sendMessage("f"); //go forward
                    Main.sendMessage("g"); //stop backward
                    Main.sendMessage("l"); //go left
                    Main.sendMessage("j"); //stop right
                    lastState = state;
                }

            }else if(y > 1){
                state = "MOVING_FORWARD_RIGHT";
                if(!lastState.equals(state)){
                    stateText.setText(state);
                    Main.sendMessage("f"); //go forward
                    Main.sendMessage("g"); //stop backward
                    Main.sendMessage("h"); //stop left
                    Main.sendMessage("r"); //go right
                    lastState = state;
                }
            }else{
                state = "MOVING_FORWARD";
                if(!lastState.equals(state)) {
                    stateText.setText(state);
                    Main.sendMessage("f"); //go forward
                    Main.sendMessage("g"); //stop backward
                    Main.sendMessage("h"); //stop left
                    Main.sendMessage("j"); //stop right
                    lastState = state;
                }
            }
        }else if(z < 0){
            if(y <= -1){

                state = "MOVING_BACKWARD_LEFT";
                if(!lastState.equals(state)){
                    stateText.setText(state);
                    Main.sendMessage("k"); //stop forward
                    Main.sendMessage("b"); //go backward
                    Main.sendMessage("l"); //go left
                    Main.sendMessage("j"); //stop right
                    lastState = state;
                }

            }else if(y > 1){
                state = "MOVING_BACKWARD_RIGHT";
                if(!lastState.equals(state)){
                    stateText.setText(state);
                    Main.sendMessage("k"); //stop forward
                    Main.sendMessage("b"); //go backward
                    Main.sendMessage("h"); //stop left
                    Main.sendMessage("r"); //go right
                    lastState = state;
                }
            }else {
                state = "MOVING_BACKWARD";
                if (!lastState.equals(state)) {
                    stateText.setText(state);
                    Main.sendMessage("k"); //stop forward
                    Main.sendMessage("b"); //go backward
                    Main.sendMessage("h"); //stop left
                    Main.sendMessage("j"); //stop right
                    lastState = state;
                }
            }
        }else{
            state = "STAY";
            if(!lastState.equals(state)) {
                stateText.setText(state);
                stop();
                lastState = state;
            }
        }

       /* if (y <= -3) {
            state = "MOVING_LEFT";
            if(!lastState.equals(state)){
                goLeft();
            }
            stateText.setText(state);
        }*/

    }

    @Override
    public void onResume() {
        super.onResume();
        senMng.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        super.onStop();
        Main.sendMessage("k");
        Main.sendMessage("g");
        Main.sendMessage("j");
        Main.sendMessage("h");
        Main.sendMessage("v");
        senMng.unregisterListener(this, acc);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use
    }
}
