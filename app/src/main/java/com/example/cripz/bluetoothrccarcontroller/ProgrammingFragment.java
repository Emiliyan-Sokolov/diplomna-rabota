package com.example.cripz.bluetoothrccarcontroller;


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
    private Button buttonNew;
    private Button buttonOpen;

    HashMap<String, String> programsConfig = new HashMap<>();

    public void fileWrite(String fileName, String text) {
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

    public void showFilesInDirectory(String filesType) {
        File programs[] = new File(getActivity().getFilesDir().getPath()).listFiles();
        if (programs.length > 0) {
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
        }
        else {
            new MaterialDialog.Builder(getActivity())
                    .title("No available programs.")
                    .neutralText("OK")
                    .show();
        }

    }

    public void fileRead(String fileName) {
        File file = new File(getActivity().getFilesDir().getPath(), fileName);

        //Read text from file
        String text = "";
        String action = "";
        JSONObject obj = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text = text + line.toString();
                text = text + "\n";
            }
            br.close();
            obj = new JSONObject(text);
            action = (String)obj.get("action");

        } catch (IOException e) {
            Toast.makeText(getActivity(),"Can't open file",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView fContent = (TextView) rootView.findViewById(R.id.fileContent);
        fContent.setText("FileContent: \n" + obj);

    }

    private void buttonInit() {
        buttonNew = (Button) rootView.findViewById(R.id.new_btn);
        buttonOpen = (Button) rootView.findViewById(R.id.open_btn);
        buttonOpen.setOnClickListener(new View.OnClickListener(){

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

        programsConfig.put("action", "go_forward");
        programsConfig.put("another_option", "test");
        programsConfig.put("state", "asd");


        JSONObject jsonObject = new JSONObject(programsConfig);
        String jsonString = jsonObject.toString();
        fileWrite("newProgram.json", jsonString);
    }
}