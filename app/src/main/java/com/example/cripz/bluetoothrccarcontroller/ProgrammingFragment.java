package com.example.cripz.bluetoothrccarcontroller;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ProgrammingFragment extends Fragment {
    private View rootView;
    private Button buttonNew;
    private Button buttonOpen;

    private void buttonInit(){
        buttonNew = (Button)rootView.findViewById(R.id.new_btn);
        buttonOpen = (Button)rootView.findViewById(R.id.open_btn);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.programming_fragment,container,false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buttonInit();
    }
}
