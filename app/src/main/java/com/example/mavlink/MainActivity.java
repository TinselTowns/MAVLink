package com.example.mavlink;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements Joystick.JoystickListener {

    Clients mClients;
    static Joystick mJoystick;
    private TextView tv_status;
    float[] pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mJoystick = new Joystick(this);

//
        setContentView(R.layout.activity_main);
        mJoystick = findViewById(R.id.game_view);
        mClients = new Clients("192.168.249.122", 8888, mJoystick);
        mClients.start();
        tv_status = findViewById(R.id.position);
        thread.start();
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
    public void onJoystickMoved(float xPercent, float yPercent, float x2Percent, float y2Percent, int id) {
        Log.d("Main_Method", "X " + xPercent + " Y " + yPercent + " " + x2Percent + " " + y2Percent);
//        tv_status.setText("X " + xPercent + " Y " + yPercent + " " + x2Percent + " " + y2Percent);
    }

    public static void UpdatePicture(Bitmap bitmap) {
        mJoystick.drawBitmap(bitmap);
    }

    public static void UpdatePosition(String positions) {
        mJoystick.printPosition(positions);
    }


}