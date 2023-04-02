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

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    Clients mClients;
    static Joystick Joystick01;
    static Joystick Joystick02;
    public static TextView tv_status;
    private TextView version;
    static String pos="";
    public static final int REQUEST_CODE=1;
    private Button mButton;
    Pictures mPictures;
    public int[] IP= {192,168,200,200};
    public static String curIP="192.168.200.200";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Joystick01 = findViewById(R.id.game_view);
        Joystick02=findViewById(R.id.game_view2);
        ImageView image=findViewById(R.id.image);
        mClients = new Clients(curIP, 8888, Joystick01, Joystick02);
        mClients.start();
        mClients.getSocket().observeForever(new Observer<DatagramSocket>() {
            @Override
            public void onChanged(DatagramSocket socket) {
                mPictures = new Pictures(socket);
                mPictures.start();
                mPictures.getData().observeForever(image::setImageBitmap);
            }
        });
        tv_status = findViewById(R.id.position);
        version=findViewById(R.id.version);
        mClients.getPosition().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                pos=s;
            }
        });

        thread.start();
        mButton=(Button)findViewById(R.id.change);
        mButton.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, ChangeIP.class);
            startActivityForResult(intent,REQUEST_CODE);
        });
    }

    private Thread thread = new Thread(){
        synchronized
        @Override
        public void run() {
            super.run();
            while (true){
                if (Joystick01 != null){
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    tv_status.setText(pos);
                                    String versionStr=Clients.getVersion();
                                    if(versionStr.equals("Ищем квадрокоптер"))
                                    {
                                        version.setText(versionStr+" "+curIP);
                                    }
                                    else version.setText(Clients.getVersion());
                                }
                            });
                    try {
                        wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        IP = data.getIntArrayExtra("ip");
        Log.d("IP",Integer.toString(IP[0]));
        curIP=IP[0]+"."+IP[1]+"."+IP[2]+"."+IP[3];
    }

}