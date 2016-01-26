package com.example.cripz.bluetoothrccarcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SetupBlocksFragment extends Fragment {

    View rootView;

    String action = "no_action";
    HashMap<String, String> programsConfig = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.setup_blocks_fragment, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initButtons();
    }

    private void initButtons() {
        MyClickListener clickListener = new MyClickListener();
        Button action = (Button) rootView.findViewById(R.id.btn_action);
        Button condition = (Button) rootView.findViewById(R.id.btn_condition);
        Button event = (Button) rootView.findViewById(R.id.btn_event);
        Button play = (Button) rootView.findViewById(R.id.btn_play);
        Button stop = (Button) rootView.findViewById(R.id.btn_stop);
        Button save = (Button) rootView.findViewById(R.id.btn_save);
        action.setOnClickListener(clickListener);
        condition.setOnClickListener(clickListener);
        event.setOnClickListener(clickListener);
        play.setOnClickListener(clickListener);
        stop.setOnClickListener(clickListener);
        save.setOnClickListener(clickListener);
    }

    private void setAction() {
        String[] actions = {"go_forward", "go_backward", "go_forward_right", "go_backward_right", "go_forward_left",
                "go_backward_left", "lights_on", "lights_off", "stay"};
        new MaterialDialog.Builder(getActivity())
                .title("Actions")
                .items(actions)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                action = "go_forward";
                                break;
                            case 1:
                                action = "go_backward";
                                break;
                            case 2:
                                action = "go_forward_right";
                                break;
                            case 3:
                                action = "go_backward_right";
                                break;
                            case 4:
                                action = "go_forward_left";
                                break;
                            case 5:
                                action = "go_backward_left";
                                break;
                            case 6:
                                action = "lights_on";
                                break;
                            case 7:
                                action = "lights_off";
                                break;
                            case 8:
                                action = "stay";
                                break;
                        }
                        programsConfig.put("action", action);
                    }
                })
                .show();
    }

    /* private void setEvent() {
         String[] sensors = {"distance", "light"};
         String[] signs = {"<",">","="};
         boolean wrapInScrollView = true;
         new MaterialDialog.Builder(getActivity())
                 .title("Events")
                 .customView(R.layout.events_view, wrapInScrollView)
                 //.items(sensors) not works when custom view is set
                 .neutralText("OK")
                 .show();
     }
 */
    private void fileWrite(String fileName, String text) {
        try {
            File file = new File(getActivity().getFilesDir().getPath(), fileName);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_action:
                    setAction();
                    break;
                case R.id.btn_event:
                    //setEvent();
                    break;
                case R.id.btn_save:
                    new MaterialDialog.Builder(getActivity())
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input("Program name", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    JSONObject jsonObject = new JSONObject(programsConfig);
                                    String jsonString = jsonObject.toString();
                                    fileWrite(input.toString() + ".json", jsonString);
                                }
                            }).show();
                    break;
            }
        }
    }
}
