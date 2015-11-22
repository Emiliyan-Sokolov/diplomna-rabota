package com.example.porsh.accelerometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private float x, y, z;
    private TextView state;
    private float x0, y0, z0;
    private Sensor acc;
    private SensorManager senMng;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senMng = (SensorManager)getSystemService(SENSOR_SERVICE);
        acc = senMng.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(acc != null){
            senMng.registerListener(this, acc, SensorManager.SENSOR_DELAY_FASTEST);

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

        if(flag == 0) {
            //x0Text = events.values[0];
            x0 = Math.round(event.values[0]);
            y0 = Math.round(event.values[1]);
            z0 = Math.round(event.values[2]);
            flag = 1;
        }
        System.out.println("X: " + x + "X0: " + x0);
        System.out.println("Y: " + y + "Y0: " + y0);
        System.out.println("Z: " + z + "Z0: " + z0);
        state = (TextView)findViewById(R.id.state);

        if(x < x0 - 1){
            state.setText("MOVING_FORWARD");
        }else if(x > x0 + 1){
            state.setText("MOVING_BACKWARD");
        }else if(y < -1){
            state.setText("MOVING_LEFT");
        }else if(y > 1){
            state.setText("MOVING_RIGHT");
        }else{
            state.setText("STAY");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use
    }
}
