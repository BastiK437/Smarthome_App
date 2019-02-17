/*
package com.example.smarthomecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BT_Connect implements Runnable{

    private BluetoothSocket btSocket;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice controller;


    public void run(){
        activity.runOnUiThread(new Runnable(){

        })
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> btDevicesSet =  btAdapter.getBondedDevices();

        for(BluetoothDevice b : btDevicesSet){
            if(b.getAddress().equals("20:16:05:26:33:92")){
                controller = b;
            }
        }

        try {
            btSocket = controller.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
*/
