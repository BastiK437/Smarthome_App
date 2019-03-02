package com.example.smarthomecontrol;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final Bluetooth btDevice = new Bluetooth(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMainMenu();

        btDevice.connect();

        findViewById(R.id.musik_menu_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, MusicControl.class);
                i.putExtra("btDevice", (Parcelable) btDevice);
                startActivity(i);
            }
        });

        findViewById(R.id.reload_main_menu_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btDevice.connect();
            }
        });

    }

    private void createMainMenu(){
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        int screenWith = screenSize.x / 3;

        Button menu_button = findViewById(R.id.tv_menu_button);
        menu_button.setWidth(screenWith);
        menu_button.setHeight(screenWith);

        menu_button = findViewById(R.id.musik_menu_button);
        menu_button.setWidth(screenWith);
        menu_button.setHeight(screenWith);

        menu_button = findViewById(R.id.relais_menu_button);
        menu_button.setWidth(screenWith);
        menu_button.setHeight(screenWith);

        menu_button = findViewById(R.id.terminal_menu_button);
        menu_button.setWidth(screenWith);
        menu_button.setHeight(screenWith);
    }

    private void setBondedTextsGreen(){
        ArrayList<TextView> bondedTexts = new ArrayList<>();

        bondedTexts.add((TextView)findViewById(R.id.bt_bonded_text_main_menu));
        bondedTexts.add((TextView)findViewById(R.id.bt_bonded_text_music_control));

        for(TextView tmp : bondedTexts){
            if(tmp!=null){
                tmp.setTextColor(Color.GREEN);
            }
        }
    }

    private void setConnectedTextsGreen(){
        ArrayList<TextView> connectedTexts = new ArrayList<>();

        connectedTexts.add((TextView)findViewById(R.id.bt_connected_text_main_menu));
        connectedTexts.add((TextView)findViewById(R.id.bt_connected_text_music_control));

        for(TextView tmp : connectedTexts){
            if(tmp!=null){
                tmp.setTextColor(Color.GREEN);
            }
        }
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
        //btDevice.close();
    }

}

/*
        Spinner spinner = (Spinner) findViewById(R.id.volume_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.volume_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        */
//spinner.setSelection(0);
//spinner.getSelectedItemPosition();
