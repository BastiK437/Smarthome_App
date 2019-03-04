package com.example.smarthomecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Pipe;
import java.util.Set;
import java.util.UUID;

public class MusicControl extends AppCompatActivity {

    private Switch powerSwitch;
    static BluetoothSocket btSocket = null;
    private OutputStream btOutput;
    private boolean isBonded;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_control);
        setButtonEffect();

        powerSwitch = findViewById(R.id.power_switch);


        if(btSocket==null){
            Intent i = new Intent(MusicControl.this, MainActivity.class);
            startActivity(i);
        }else{
            TextView bonded = findViewById(R.id.bt_bonded_text_music_control);
            bonded.setTextColor(Color.GREEN);
            try {
                btOutput = btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(btSocket.isConnected()){
                TextView btConnectedText = findViewById(R.id.bt_connected_text_music_control);
                btConnectedText.setTextColor(Color.GREEN);
            }else{
                BT_Connect connect = new BT_Connect(btSocket);
                connect.start();
            }
        }


        final Spinner spinner = (Spinner) findViewById(R.id.volume_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.volume_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setBackgroundColor(Color.GRAY);

        findViewById(R.id.aux_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btSocket.isConnected()){
                    powerSwitch.setChecked(true);
                    send("aux");
                    addTerminalText("send 'aux'");
                }else{
                    addTerminalText("not connected");
                }
            }
        });


        findViewById(R.id.tape_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btSocket.isConnected()){
                    powerSwitch.setChecked(true);
                    send("tape");
                    addTerminalText("send 'tape'");
                }else{
                    addTerminalText("not connected");
                }
            }
        });


        findViewById(R.id.dvd_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btSocket.isConnected()){
                    powerSwitch.setChecked(true);
                    send("dvd");
                    addTerminalText("send 'dvd'");
                }else{
                    addTerminalText("not connected");
                }
            }
        });


        findViewById(R.id.power_switch).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btSocket.isConnected()){
                    String buffer;
                    if(powerSwitch.isChecked()){
                        buffer = "anlage an";
                    }else{
                        buffer = "anlage aus";
                    }
                    send(buffer);
                    addTerminalText("send '" + buffer + "'");
                }else{
                    powerSwitch.setChecked(false);
                    addTerminalText("not connected");
                }

            }
        });

        findViewById(R.id.reload_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btSocket.isConnected()){
                    TextView btConnectedText = findViewById(R.id.bt_connected_text_music_control);
                    btConnectedText.setTextColor(Color.GREEN);
                    addTerminalText("Connected");
                }else{
                    addTerminalText("Connecting");
                    BT_Connect connect = new BT_Connect(btSocket);
                    connect.start();
                }
            }
        });

        findViewById(R.id.volume_down_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btSocket.isConnected()){
                    if(powerSwitch.isChecked()){
                        String buffer;
                        if(spinner.getSelectedItemPosition() == 0){
                            buffer = "aleiser";
                        }else{
                            buffer = "am5";
                        }
                        addTerminalText("send '" + buffer + "'");
                        send(buffer);
                    }else{
                        addTerminalText("turn it on first");
                    }
                }else{
                    addTerminalText("not connected");
                }
            }
        });

        findViewById(R.id.volume_up_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btSocket.isConnected()){
                    if(powerSwitch.isChecked()){
                        String buffer;
                        if(spinner.getSelectedItemPosition() == 0){
                            buffer = "alauter";
                        }else{
                            buffer = "ap5";
                        }
                        addTerminalText("send '" + buffer + "'");
                        send(buffer);
                    }else{
                        addTerminalText("turn it on first");
                    }
                }else{
                    addTerminalText("not connected");
                }
            }
        });
    }

    private void send(String command){
        try {
            btOutput.write(command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTerminalText(String text){

        View child = getLayoutInflater().inflate(R.layout.terminal_message, null);
        TextView terminal_text = child.findViewById(R.id.terminal_text);
        terminal_text.setText(text);

        LinearLayout terminal = findViewById(R.id.terminal);
        terminal.addView(child);

        final ScrollView sv = findViewById(R.id.terminal_scroll);
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void setButtonEffect(){
        buttonEffect(findViewById(R.id.dvd_button));
        buttonEffect(findViewById(R.id.aux_button));
        buttonEffect(findViewById(R.id.tape_button));
        buttonEffect(findViewById(R.id.reload_button));
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
}
