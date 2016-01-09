package com.example.cripz.bluetoothrccarcontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.view.View.OnTouchListener;

/**
 * Created by porsh on 1/9/2016.
 */
public class Programming extends AppCompatActivity {
    private Button buttonNew;
    private Button buttonOpen;

    private void buttonInit(){
        buttonNew = (Button)findViewById(R.id.new_btn);
        buttonOpen = (Button)findViewById(R.id.open_btn);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.programming_main);


    }
}
