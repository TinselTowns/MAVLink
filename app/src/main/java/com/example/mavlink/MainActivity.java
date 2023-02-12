package com.example.mavlink;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements Joystick.JoystickListener {

    Clients mClients;
    static Joystick mJoystick;
    private TextView tv_status;
    float[] pos;
    public static final int REQUEST_CODE=1;
    private Button mButton;
    public int[] IP= {192,168,200,200};
    public static String curIP="192.168.200.200";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mJoystick = new Joystick(this);

//
        setContentView(R.layout.activity_main);
        mJoystick = findViewById(R.id.game_view);
        mClients = new Clients(curIP, 8888, mJoystick);
        mClients.start();
        tv_status = findViewById(R.id.position);
        thread.start();
        mButton=(Button)findViewById(R.id.change);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ChangeIP.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }



    private Thread thread = new Thread(){
        synchronized
        @Override
        public void run() {
            super.run();
            while (true){
                if (mJoystick != null){
                    pos = mJoystick.getPosition();
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    tv_status.setText("X " + pos[0] + " Y " + pos[1] + " " + pos[2] + " " + pos[3]);
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



    @Override
    public void onJoystickMoved(float xPercent, float yPercent, float x2Percent, float y2Percent, int id) {
    }

    public static void UpdatePicture(Bitmap bitmap) {
        mJoystick.drawBitmap(bitmap);
    }

    public static void UpdatePosition(String positions) {
        mJoystick.printPosition(positions);
    }


}