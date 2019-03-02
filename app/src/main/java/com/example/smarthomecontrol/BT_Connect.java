
package com.example.smarthomecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BT_Connect extends Thread{

    private BluetoothSocket btSocket;

    public BT_Connect(BluetoothSocket socket){
        btSocket = socket;
    }

    public void run(){
        try {
            btSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

