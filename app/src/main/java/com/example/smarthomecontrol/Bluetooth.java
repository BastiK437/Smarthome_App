package com.example.smarthomecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class Bluetooth implements Parcelable {
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice controller;
    private BluetoothSocket btSocket;
    private OutputStream btOutput;
    private Activity activity;
    private boolean isBonded;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    return;
                }
            }
        }
    };

    public Bluetooth(Activity activity){
        this.activity=activity;
        isBonded = false;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> btDevicesSet =  btAdapter.getBondedDevices();

        for(BluetoothDevice b : btDevicesSet){
            if(b.getAddress().equals("20:16:05:26:33:92")){
                //TextView bondedBT = findViewById(R.id.bt_bonded_text);
                //bondedBT.setTextColor(Color.GREEN);
                isBonded = true;
                controller = b;
                break;
            }
        }

        //activity.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        try {
            btSocket = controller.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            btOutput = btSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connect(){
        BT_Connect connectThread = new BT_Connect(btSocket);
        connectThread.start();

        return true;
    }

    public void write(String command){
        try {
            btOutput.write(command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return btSocket.isConnected();
    }

    public OutputStream getBtOutput(){
        return btOutput;
    }

    public boolean isBonded(){
        return isBonded;
    }

    public BluetoothSocket getBtSocket(){
        return btSocket;
    }

    public void close(){
        try {
            btOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Bluetooth(Parcel in) {
        btAdapter = (BluetoothAdapter) in.readValue(BluetoothAdapter.class.getClassLoader());
        controller = (BluetoothDevice) in.readValue(BluetoothDevice.class.getClassLoader());
        btSocket = (BluetoothSocket) in.readValue(BluetoothSocket.class.getClassLoader());
        btOutput = (OutputStream) in.readValue(OutputStream.class.getClassLoader());
        activity = (Activity) in.readValue(Activity.class.getClassLoader());
        isBonded = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(btAdapter);
        dest.writeValue(controller);
        dest.writeValue(btSocket);
        dest.writeValue(btOutput);
        dest.writeValue(activity);
        dest.writeByte((byte) (isBonded ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Bluetooth> CREATOR = new Parcelable.Creator<Bluetooth>() {
        @Override
        public Bluetooth createFromParcel(Parcel in) {
            return new Bluetooth(in);
        }

        @Override
        public Bluetooth[] newArray(int size) {
            return new Bluetooth[size];
        }
    };
}