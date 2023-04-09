package com.example.mavlink;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class Joystick extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    public static float[] pos = new float[2];
    float x1 = centerX;
    float y1 = centerY;


    public MutableLiveData<float[]> liveData = new MutableLiveData<>();

    LiveData<float[]> getData() {
        return liveData;
    }

    private void setUp() {
        centerX = Math.min(getWidth(), getHeight()) / 2;
        centerY = getHeight() - Math.min(getWidth(), getHeight()) / 2;
        baseRadius = 40 * Math.min(getWidth(), getHeight()) / 100;
        hatRadius = Math.min(getWidth(), getHeight()) / 10;
    }

    public Joystick(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);

    }

    public Joystick(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        getHolder().addCallback(this);
        setOnTouchListener(this);
    }

    public Joystick(Context context, AttributeSet attributes) {
        super(context, attributes);
        getHolder().addCallback(this);
        setOnTouchListener(this);
    }


    public Canvas myCanvas = null;

    public void drawJoystick(float newX, float newY) {
        if (getHolder().getSurface().isValid()) {
            curX = x1 = newX;
            curY = y1 = newY;
            myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.WHITE);
            colors.setARGB(180, 50, 50, 50);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            colors.setARGB(255, 255, 100, 0);
            myCanvas.drawCircle(newX, newY, hatRadius, colors);
            getHolder().unlockCanvasAndPost(myCanvas);
            pos[0] = (x1 - centerX) * 500 / baseRadius + 1500;
            pos[1] = -(y1 - centerY) * 500 / baseRadius + 1500;
            liveData.setValue(pos);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setUp();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    boolean inTouch = false;
    float zeroX = 0;
    float zeroY = 0;
    public float curX = centerX;
    public float curY = centerY;
    int index1 = -1;


    public boolean onTouch(View v, MotionEvent e) {
        if (v.equals(this)) {
            int actionMask = e.getActionMasked();
            switch (actionMask) {
                case MotionEvent.ACTION_DOWN:
                    zeroX = e.getX(0);
                    zeroY = e.getY(0);
                    if ((float) Math.sqrt((Math.pow(e.getX(0) - centerX, 2)) + Math.pow(e.getY(0) - centerY, 2)) < baseRadius) {
                        inTouch = true;
                        index1 = 0;
                        zeroX = e.getX(0);
                        zeroY = e.getY(0);
                        curX = centerX;
                        curY = centerY;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    inTouch = false;
                    index1 = -1;
                    zeroX = centerX;
                    zeroY = centerY;
                    drawJoystick(centerX, centerY);

                case MotionEvent.ACTION_MOVE:
                    if (index1 != -1) {
                        curX = e.getX(index1);
                        curY = e.getY(index1);
                        float displacement = (float) Math.sqrt((Math.pow(e.getX(index1) - centerX, 2)) + Math.pow(e.getY(index1) - centerY, 2));
                        if (displacement > baseRadius) {
                            float dis = (float) Math.sqrt((Math.pow(e.getX(index1) - centerX, 2)) + Math.pow(e.getY(index1) - centerY, 2));
                            float ratio = baseRadius / dis;
                            curX = centerX + (e.getX(index1) - centerX) * ratio;
                            curY = centerY + (e.getY(index1) - centerY) * ratio;
                        }
                        drawJoystick(curX, curY);
                    }
                    break;
            }
        }
        return true;
    }
}