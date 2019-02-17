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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.textclassifier.TextClassificationManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice controller;
    private BluetoothSocket btSocket;
    private OutputStream btOutput;
    private Switch powerSwitch;
    private LinearLayout terminal;

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
    protected void onDestroy(){
        super.onDestroy();
        try {
            btOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        powerSwitch = findViewById(R.id.power_switch);
        terminal = findViewById(R.id.terminal);

        Spinner spinner = (Spinner) findViewById(R.id.volume_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.volume_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //spinner.setSelection(0);
        //spinner.getSelectedItemPosition();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> btDevicesSet =  btAdapter.getBondedDevices();

        for(BluetoothDevice b : btDevicesSet){
            if(b.getAddress().equals("20:16:05:26:33:92")){
                TextView bondedBT = findViewById(R.id.bt_bonded_text);
                bondedBT.setTextColor(Color.GREEN);
                controller = b;
            }
        }

        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        try {
            btOutput = btSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        findViewById(R.id.aux_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    powerSwitch.setChecked(true);
                    String aux = "aux";
                    btOutput.write(aux.getBytes());
                    //btOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.tape_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    powerSwitch.setChecked(true);
                    String aux = "tape";
                    btOutput.write(aux.getBytes());
                    //btOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.dvd_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    powerSwitch.setChecked(true);
                    String aux = "dvd";
                    btOutput.write(aux.getBytes());
                    //btOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        findViewById(R.id.power_switch).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String buffer;
                if(powerSwitch.isChecked()){
                    buffer = "anlage an";
                }else{
                    buffer = "anlage aus";
                }
                try {
                    btOutput.write(buffer.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.reload_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                addTerminalText("Connecting");
                try {
                    btSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(btSocket.isConnected()){
                    TextView btConnectedText = findViewById(R.id.bt_connected_text);
                    btConnectedText.setTextColor(Color.GREEN);
                    addTerminalText("Connected");
                }else{
                    addTerminalText("Not connected, try again");
                }
            }
        });

    }



    private void addTerminalText(String text){
//        View child = getLayoutInflater().inflate(R.layout.terminal_message, null);
//        TextView terminal_text = child.findViewById(R.id.terminal_text);
//        terminal_text.setText(text);
//        terminal.addView(child);
//
//        ScrollView sv = findViewById(R.id.terminal_scroll);
//        sv.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (! (requestCode == REQUEST_ENABLE_BT  && resultCode  == RESULT_OK) ) {
                System.exit(0);
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
