package com.example.mavlink;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.widget.TextView;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.common.CommandLong;


public class MainActivity extends AppCompatActivity {

    Clients mClients;
    static Joystick Joystick01;
    static Joystick Joystick02;
    public static TextView tv_status;
    private TextView version;
    public static final int REQUEST_CODE = 1;
    private Button mButton;
    Pictures mPictures;
    public int[] IP = {192, 168, 200, 200};
    public static String curIP = "192.168.200.200";
    String flexibleVersion = "";
    private Button startButton;
    private Button endButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Joystick01 = findViewById(R.id.game_view);
        Joystick02 = findViewById(R.id.game_view2);
        ImageView image = findViewById(R.id.image);
        mClients = new Clients(curIP, 8888, Joystick01, Joystick02);
        mClients.start();
        mClients.getSocket().observeForever(socket -> {
            mPictures = new Pictures(socket);
            mPictures.start();
            mPictures.getData().observeForever(image::setImageBitmap);
        });
        tv_status = findViewById(R.id.position);
        version = findViewById(R.id.version);
        version.setText("Ищем квадрокоптер " + curIP);
        mClients.getPosition().observeForever(s -> tv_status.setText(s));
        mClients.getFlexibleVersion().observeForever(s -> {
            if (!s.equals("")) {
                flexibleVersion = s;
                version.setText(s);
            }
        });

        mButton = findViewById(R.id.change);
        mButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChangeIP.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(v -> mClients.Drone(1));

        endButton = findViewById(R.id.end);
        endButton.setOnClickListener(v -> mClients.Drone(0));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        IP = data.getIntArrayExtra("ip");
        Log.d("IP", Integer.toString(IP[0]));
        curIP = IP[0] + "." + IP[1] + "." + IP[2] + "." + IP[3];
        if (flexibleVersion.equals("")) {
            version.setText("Ищем квадрокоптер " + curIP);
        }
    }

}