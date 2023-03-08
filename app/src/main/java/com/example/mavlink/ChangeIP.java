package com.example.mavlink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeIP extends AppCompatActivity {

    public static int[] IP = {192, 168, 200, 200};
    EditText text1 = null;
    EditText text2 = null;
    EditText text3 = null;
    EditText text4 = null;

    private Button mButton;


    private void newIntent(int[] newIP) {
        Intent intent = new Intent();
        intent.putExtra("ip", newIP);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_ip);
        text1 = findViewById(R.id.ip1);
        text2 = findViewById(R.id.ip2);
        text3 = findViewById(R.id.ip3);
        text4 = findViewById(R.id.ip4);

        try {
            text1.setText(Integer.toString(IP[0]));
            text2.setText(Integer.toString(IP[1]));
            text3.setText(Integer.toString(IP[2]));
            text4.setText(Integer.toString(IP[3]));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("erk", e.toString());
        }

        mButton = (Button) findViewById(R.id.back);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text1.getText().toString().equals("") && !text2.getText().toString().equals("") && !text3.getText().toString().equals("") && !text4.getText().toString().equals("")) {
                    IP[0] = Integer.valueOf(text1.getText().toString());
                    IP[1] = Integer.valueOf(text2.getText().toString());
                    IP[2] = Integer.valueOf(text3.getText().toString());
                    IP[3] = Integer.valueOf(text4.getText().toString());
                    if (IP[0] <= 255 && IP[1] <= 255 && IP[2] <= 255 && IP[3] <= 255)
                        newIntent(IP);
                }
            }
        });

    }


}