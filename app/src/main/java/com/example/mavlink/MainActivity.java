package com.example.mavlink;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity implements Joystick.JoystickListener {

    Clients mClients;
    static Joystick mJoystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mJoystick = new Joystick(this);
        mClients = new Clients("192.168.1.51", 8888, mJoystick);
        mClients.start();
        setContentView(mJoystick);


    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, float x2Percent, float y2Percent, int id) {
        Log.d("Main Method", "X " + xPercent + " Y " + yPercent + " " + x2Percent + " " + y2Percent);
    }

    public static void UpdatePicture(Bitmap bitmap) {
        mJoystick.drawBitmap(bitmap);
    }

    public static void UpdatePosition(String positions) {
        mJoystick.printPosition(positions);
    }


}