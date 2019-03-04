package com.example.smarthomecontrol;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    private BluetoothDevice controller;
    private BluetoothSocket btSocket;
    private OutputStream btOutput;
    private boolean isBonded;
    private BT_Connect connecting;


    private final static int REQUEST_ENABLE_BT = 1;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    return;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        createMainMenu();
        createSocket();
        setButtonEffect();

        if(btSocket.isConnected()){
            TextView btConnectedText = findViewById(R.id.bt_connected_text_main_menu);
            btConnectedText.setTextColor(Color.GREEN);
        }else{
            connecting = new BT_Connect(btSocket);
            connecting.start();
        }

        findViewById(R.id.musik_menu_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                MusicControl musicControl = new MusicControl();
                musicControl.btSocket = btSocket;
                Intent i = new Intent(MainActivity.this, MusicControl.class);
                startActivity(i);
            }
        });

        findViewById(R.id.reload_main_menu_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btSocket.isConnected()){
                    TextView btConnectedText = findViewById(R.id.bt_connected_text_main_menu);
                    btConnectedText.setTextColor(Color.GREEN);
                }else{
                    connecting.start();
                }
            }
        });

    }

    private void setButtonEffect(){
        buttonEffect(findViewById(R.id.reload_main_menu_button));
        buttonEffect(findViewById(R.id.musik_menu_button));
        buttonEffect(findViewById(R.id.tv_menu_button));
        buttonEffect(findViewById(R.id.relais_menu_button));
        buttonEffect(findViewById(R.id.terminal_menu_button));
    }

    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
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

    private void createSocket(){
        Set<BluetoothDevice> btDevicesSet =  btAdapter.getBondedDevices();
        for(BluetoothDevice b : btDevicesSet){
            if(b.getAddress().equals("20:16:05:26:33:92")){
                TextView bonded = findViewById(R.id.bt_bonded_text_main_menu);
                bonded.setTextColor(Color.GREEN);
                controller = b;
                break;
            }
        }
        registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        try {
            btSocket = controller.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            btOutput = btSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            btOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
