package com.example.mavlink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    TextView errorIP=null;
    boolean correctIP = true;

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
        errorIP=findViewById(R.id.incorrect);

        try {
            text1.setText(Integer.toString(IP[0]));
            text2.setText(Integer.toString(IP[1]));
            text3.setText(Integer.toString(IP[2]));
            text4.setText(Integer.toString(IP[3]));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("erk", e.toString());
        }

        text1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("smth", s.toString());
                if (s.toString().equals("")) {
                    correctIP = false;
                } else {
                    if (Integer.valueOf(s.toString()) > 255) {
                        correctIP = false;
                    } else correctIP = true;
                }
                onChangedIP();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        text2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("smth", s.toString());
                if (s.toString().equals("")) {
                    correctIP = false;
                } else {
                    if (Integer.valueOf(s.toString()) > 255) {
                        correctIP = false;
                    } else correctIP = true;
                }
                onChangedIP();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        text3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("smth", s.toString());
                if (s.toString().equals("")) {
                    correctIP = false;
                } else {
                    if (Integer.valueOf(s.toString()) > 255) {
                        correctIP = false;
                    } else correctIP = true;
                }
                onChangedIP();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        text4.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("smth", s.toString());
                if (s.toString().equals("")) {
                    correctIP = false;
                } else {
                    if (Integer.valueOf(s.toString()) > 255) {
                        correctIP = false;
                    } else correctIP = true;
                }
                onChangedIP();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        mButton = (Button) findViewById(R.id.back);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (correctIP) {
                    IP[0] = Integer.valueOf(text1.getText().toString());
                    IP[1] = Integer.valueOf(text2.getText().toString());
                    IP[2] = Integer.valueOf(text3.getText().toString());
                    IP[3] = Integer.valueOf(text4.getText().toString());
                    newIntent(IP);
                }
            }
        });
    }
    private void onChangedIP()
    {
        if(!correctIP)
        {
            errorIP.setText("IP адрес введен некорректно");
        }
        else errorIP.setText("");
    }


}