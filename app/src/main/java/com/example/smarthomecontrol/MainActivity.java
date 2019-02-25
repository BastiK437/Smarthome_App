package com.example.smarthomecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Bluetooth btDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btDevice = new Bluetooth(this);

        /*
        Spinner spinner = (Spinner) findViewById(R.id.volume_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.volume_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        */
        //spinner.setSelection(0);
        //spinner.getSelectedItemPosition();



        createMainMenu();

        Intent i = new Intent(MainActivity.this, MusicControl.class);
        startActivity(i);

        findViewById(R.id.musik_menu_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            Intent i = new Intent(MainActivity.this, MusicControl.class);
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

    public Bluetooth getBtDevice(){
        return btDevice;
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            btDevice.getBtOutput().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
