package com.example.mavlink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity implements Joystick.JoystickListener{

    Clients mClients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClients=new Clients("127.0.0.1",8888);
        mClients.run();
        Joystick joystick = new Joystick(this);
        setContentView(joystick);


    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent,int id)
    {
        Log.d("Main Method", "X "+xPercent+" Y "+yPercent);
    }








}