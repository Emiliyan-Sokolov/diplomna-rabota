package com.example.cripz.bluetoothrccarcontroller;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ProgrammingFragment extends Fragment {
    private View rootView;

    public void showFilesInDirectory(String filesType) {
        int jsonCounter = 0;
        File programs[] = new File(getActivity().getFilesDir().getPath()).listFiles();
        for (File program : programs) {
            if (program.getName().contains(filesType)) {
                jsonCounter++;
            }
        }

        if(jsonCounter > 0){
            final String prList[] = new String[jsonCounter];
            int arrayCounter = 0;
            for(File program : programs){
                if(program.getName().contains(filesType)){
                    prList[arrayCounter] = program.getName();
                    arrayCounter++;
                }
            }
            new MaterialDialog.Builder(getActivity())
                    .title("Programs")
                    .items(prList)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            fileRead(prList[which]);
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(getActivity())
                    .title("No available programs.")
                    .positiveText("OK")
                    .show();
        }

        /*
            final String prList[] = new String[programs.length];
            for (int i = 0; i < programs.length; i++) {
                if (programs[i].getName().contains(filesType)) {
                    prList[i] = programs[i].getName();
                }
            }
            new MaterialDialog.Builder(getActivity())
                    .title("Programs")
                    .items(prList)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            Log.d("err", prList[which]);
                            fileRead(prList[which]);
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(getActivity())
                    .title("No available programs.")
                    .neutralText("OK")
                    .show();
        }
        */
    }

    public String fileRead(String fileName) {
        File file = new File(getActivity().getFilesDir().getPath(), fileName);

        //Read text from file
        String text = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text = text + line.toString();
                text = text + "\n";
            }
            br.close();

        } catch (IOException e) {
            Toast.makeText(getActivity(), "Can't open file", Toast.LENGTH_SHORT).show();
        }
        

        return text;

    }

    private void buttonInit() {
        Button buttonNew = (Button) rootView.findViewById(R.id.new_btn);
        Button buttonOpen = (Button) rootView.findViewById(R.id.open_btn);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupBlocksFragment setupBlocksFragment = new SetupBlocksFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, setupBlocksFragment)
                        .commit();
            }
        });
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilesInDirectory(".json");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.programming_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buttonInit();
        /*
        programsConfig.put("action", "go_forward");
        programsConfig.put("another_option", "test");
        programsConfig.put("state", "asd");
        programsConfig.put("condition", "light");

        JSONObject jsonObject = new JSONObject(programsConfig);
        String jsonString = jsonObject.toString();
        fileWrite("newProgram.json", jsonString);

        String condition = "";
        String action = "";
        JSONObject obj = null;

        try {
            obj = new JSONObject(fileRead("newProgram.json"));
            action = (String) obj.get("action");
            condition = (String) obj.get("condition");
            switch (condition) {
                case "light":
                    Main.sendMessage("n");
                    Main.carLights.setBackgroundResource(R.drawable.short_on);
                    Main.shortLightsFlag = true;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

*/
    }

}