package com.example.smarthomecontrol;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Pipe;

public class MusicControl extends AppCompatActivity {

    private Switch powerSwitch;
    private Pipe pipe;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_control);

        powerSwitch = findViewById(R.id.power_switch);

        findViewById(R.id.aux_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                powerSwitch.setChecked(true);
                MainActivity tmp = new MainActivity();

                if(!tmp.write("aux")){
                    addTerminalText("aux not send");
                }else{
                    addTerminalText("aux");
                }
            }
        });

       /*
        findViewById(R.id.tape_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btDevice.isConnected()){
                    powerSwitch.setChecked(true);
                    btDevice.write("tape");
                }else{
                    addTerminalText("not connected");
                }
            }
        });

        findViewById(R.id.dvd_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btDevice.isConnected()){
                    powerSwitch.setChecked(true);
                    btDevice.write("dvd");
                }else{
                    addTerminalText("not connected");
                }
            }
        });


        findViewById(R.id.power_switch).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(btDevice.isConnected()){
                    String buffer;
                    if(powerSwitch.isChecked()){
                        buffer = "anlage an";
                    }else{
                        buffer = "anlage aus";
                    }
                    btDevice.write(buffer);
                }else{
                    powerSwitch.setChecked(false);
                    addTerminalText("not connected");
                }

            }
        });

        findViewById(R.id.reload_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                addTerminalText("Connecting");
                if(btDevice.isBonded()){
                    TextView btBondedText = findViewById(R.id.bt_bonded_text);
                    btBondedText.setTextColor(Color.GREEN);
                }
                //btDevice.connect();
                if(btDevice.isConnected()){
                    TextView btConnectedText = findViewById(R.id.bt_connected_text);
                    btConnectedText.setTextColor(Color.GREEN);
                    addTerminalText("Connected");
                }else{
                    addTerminalText("Not connected, try again");
                }
            }
        });*/
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
}
