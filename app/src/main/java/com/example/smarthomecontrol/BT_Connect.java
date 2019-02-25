
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

        MainActivity main = new MainActivity();
        Bluetooth btDevice = main.getBtDevice();

        try {
            btDevice.getBtSocket().connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

