package com.example.cripz.bluetoothrccarcontroller;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class ControlsFragment extends Fragment {
    private View rootView;
    private ImageButton forwardButton;
    private ImageButton reverseButton;
    private ImageButton leftButton;
    private ImageButton rightButton;

    private void sendMessage(String msg) {
        Main.sendMessage(msg);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.controls_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeButtonVariables();
    }

    @Override
    public void onStop() {
        super.onStop();
        sendMessage("k");
        sendMessage("g");
        sendMessage("j");
        sendMessage("h");
        sendMessage("v");
    }

    private void initializeButtonVariables() {
        MyTouchListener touchListener = new MyTouchListener();
        forwardButton = (ImageButton) rootView.findViewById(R.id.forward_btn);
        reverseButton = (ImageButton) rootView.findViewById(R.id.reverse_btn);
        leftButton = (ImageButton) rootView.findViewById(R.id.left_btn);
        rightButton = (ImageButton) rootView.findViewById(R.id.right_btn);
        forwardButton.setOnTouchListener(touchListener);
        reverseButton.setOnTouchListener(touchListener);
        leftButton.setOnTouchListener(touchListener);
        rightButton.setOnTouchListener(touchListener);
    }

    public class MyTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
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
            }
            return true;
        }
    }

}
