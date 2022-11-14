package com.example.mavlink;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class Joystick extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private float centerX;
    private float centerY;
    private float centerX2;
    private float centerY2;
    private float baseRadius;
    private float hatRadius;

    private void setUp() {
        centerX = getWidth() / 4;
        centerY = getHeight() / 2;
        centerX2 = 3 * getWidth() / 4;
        centerY2 = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 5;
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


    public void drawJoystick(float newX, float newY, float newX2, float newY2) {
        if (getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            colors.setARGB(255, 50, 50, 50);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            myCanvas.drawCircle(centerX2, centerY2, baseRadius, colors);


            colors.setARGB(255, 255, 100, 0);
            myCanvas.drawCircle(newX, newY, hatRadius, colors);
            myCanvas.drawCircle(newX2, newY2, hatRadius, colors);

            getHolder().unlockCanvasAndPost(myCanvas);

        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setUp();
        drawJoystick(centerX, centerY, centerX2, centerY2);



    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public float zeroX = centerX;
    public float zeroY = centerY;
    public float curX = centerX;
    public float curY = centerY;
    public boolean first = false;
    public float displacement = 0;

    public float zeroX2 = centerX2;
    public float zeroY2 = centerY2;
    public float curX2 = centerX2;
    public float curY2 = centerY2;
    public boolean second = false;
    public float displacement2 = 0;


    public boolean onTouch(View v, MotionEvent e) {
        Log.d("Main Method", "X " + e.getX() + " Y " + e.getY());
        if (v.equals(this)) {

            Log.d("center ", "X " + centerX + " Y " + centerY);
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                if (!first && (float) Math.sqrt((Math.pow(e.getX() - centerX, 2)) + Math.pow(e.getY() - centerY, 2)) < baseRadius) {
                    zeroX = e.getX();
                    zeroY = e.getY();
                    curX = e.getX();
                    curY = e.getY();
                    first = true;

                    Log.d("pos ", "X " + e.getX() + " Y " + e.getY());
                } else if (!second && (float) Math.sqrt((Math.pow(e.getX() - centerX2, 2)) + Math.pow(e.getY() - centerY2, 2)) < baseRadius) {
                    zeroX2 = e.getX();
                    zeroY2 = e.getY();
                    curX2 = e.getX();
                    curY2 = e.getY();
                    second = true;

                    Log.d("pos2 ", "X " + e.getX() + " Y " + e.getY());
                }
            } else {
                if (e.getAction() != MotionEvent.ACTION_UP && first) {

                    displacement = (float) Math.sqrt((Math.pow(e.getX() - zeroX, 2)) + Math.pow(e.getY() - zeroY, 2));


                    if (displacement > baseRadius) {
                        float dis = (float) Math.sqrt((Math.pow(e.getX() - centerX, 2)) + Math.pow(e.getY() - centerY, 2));
                        float ratio = baseRadius / dis;
                        float constrainedX = centerX + (e.getX() - centerX) * ratio;
                        float constrainedY = centerY + (e.getY() - centerY) * ratio;
                        drawJoystick(constrainedX, constrainedY,0,0);
                    } else {

                        drawJoystick(-zeroX + e.getX() + centerX, -zeroY + e.getY() + centerY,centerX2,centerY2);
                        curX = -zeroX + e.getX() + centerX;
                        curY = -zeroY + e.getY() + centerY;
                    }


                } else if(first) {
                    drawJoystick(centerX, centerY,centerX2,centerY2);
                    first = false;
                }
                if (e.getAction() != MotionEvent.ACTION_UP && second) {

                    displacement2 = (float) Math.sqrt((Math.pow(e.getX() - zeroX2, 2)) + Math.pow(e.getY() - zeroY2, 2));


                    if (displacement2 > baseRadius) {
                        float dis = (float) Math.sqrt((Math.pow(e.getX() - centerX2, 2)) + Math.pow(e.getY() - centerY2, 2));
                        float ratio = baseRadius / dis;
                        float constrainedX = centerX2 + (e.getX() - centerX2) * ratio;
                        float constrainedY = centerY2 + (e.getY() - centerY2) * ratio;
                        drawJoystick(centerX,centerY,constrainedX, constrainedY);
                    } else {

                        drawJoystick(centerX,centerY,-zeroX2 + e.getX() + centerX2, -zeroY2 + e.getY() + centerY2);
                        curX2 = -zeroX2 + e.getX() + centerX2;
                        curY2 = -zeroY2 + e.getY() + centerY2;
                    }


                } else if(second){
                    drawJoystick(centerX,centerY,centerX2, centerY2);
                    second = false;
                }
            }
        }
        return true;
    }


    public interface JoystickListener {
        void onJoystickMoved(float xPercent, float yPercent, int id);
    }
}
