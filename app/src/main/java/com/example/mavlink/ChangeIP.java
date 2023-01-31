package com.example.mavlink;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.EditText;

public class ChangeIP extends AppCompatActivity {
    private WifiManager wifiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_ip);
        EditText text1=findViewById(R.id.ip1);
        EditText text2=findViewById(R.id.ip2);
        EditText text3=findViewById(R.id.ip3);
        EditText text4=findViewById(R.id.ip4);
        wifiManager=(WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        try
        {
            String ip= Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
            String[] ipParts=ip.split("\\Q.\\E");
            text1.setText(ipParts[0]);
            text2.setText(ipParts[1]);
            text3.setText(ipParts[2]);
            text4.setText(ipParts[3]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}