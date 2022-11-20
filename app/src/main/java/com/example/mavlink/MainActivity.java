package com.example.mavlink;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity implements Joystick.JoystickListener{

    Clients mClients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Joystick joystick = new Joystick(this);
        mClients=new Clients("192.168.1.44",8888,joystick);
        mClients.start();
        setContentView(joystick);


    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent,float x2Percent,float y2Percent, int id)
    {
        Log.d("Main Method", "X "+xPercent+" Y "+yPercent+" "+x2Percent+" "+y2Percent);
    }








}