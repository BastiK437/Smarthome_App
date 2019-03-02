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
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice controller;
    private BluetoothSocket btSocket;
    private OutputStream btOutput;
    private Activity activity;
    private boolean isBonded;
    private Handler bt_handler;
    private PipedReader reader;
    private PipedWriter writer;

    private static final int HANDLER = 20;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Spinner spinner = (Spinner) findViewById(R.id.volume_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.volume_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        */
        //spinner.setSelection(0);
        //spinner.getSelectedItemPosition();

        createMainMenu();

        btStartup();

        final Bluetooth test = new Bluetooth(this);

        BT_Connect connect = new BT_Connect(btSocket);
        connect.start();

        findViewById(R.id.musik_menu_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, MusicControl.class);
                i.putExtra("btDevice", test);
                startActivity(i);
            }
        });

        findViewById(R.id.reload_main_menu_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                BT_Connect connectThread = new BT_Connect(btSocket);
                connectThread.start();
                if(btSocket.isConnected()){
                    setConnectedTextsGreen();
                    connectThread.interrupt();
                }
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

    private void btStartup(){

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Set<BluetoothDevice> btDevicesSet =  btAdapter.getBondedDevices();
        for(BluetoothDevice b : btDevicesSet){
            if(b.getAddress().equals("20:16:05:26:33:92")){
                setBondedTextsGreen();
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

        //ConnectedThread pipeline = new ConnectedThread(btSocket, reader);
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

    public boolean write(String text){
        btStartup();
        try {
            if(btSocket!=null){
                btOutput = btSocket.getOutputStream();
                btOutput.write(text.getBytes());
            }else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private class ConnectedThread extends Thread {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        PipedReader reader = null;
        int avilableBytes=0;

        public ConnectedThread(BluetoothSocket socket, PipedReader reader){
            InputStream tmpInput = null;
            OutputStream tmpOutput = null;
            try{
                tmpInput = socket.getInputStream();
                tmpOutput = socket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }
            inputStream = tmpInput;
            outputStream = tmpOutput;
            this.reader = reader;
        }

        public void run() {
            try{
                int bytes;
                while (true){
                    try{
                        avilableBytes=inputStream.available();
                        byte[] buffer=new byte[avilableBytes];
                        if (avilableBytes>0){
                            bytes=inputStream.read(buffer);
                            final String readMessage=new String(buffer);
                            if (bytes>=3){
                                bt_handler.obtainMessage(HANDLER, bytes, -1, readMessage).sendToTarget();
                            }
                            else {
                                SystemClock.sleep(100);
                            }
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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
