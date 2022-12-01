package com.example.mavlink;


import android.content.Context;
import android.graphics.Bitmap;
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
    public static float[] pos = new float[4];
    float x1 = centerX;
    float y1 = centerY;
    float x2 = centerX;
    float y2 = centerY2;


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

public Canvas myCanvas=null;
    public void drawJoystick(float newX, float newY, float newX2, float newY2) {
        if (getHolder().getSurface().isValid()) {
            x1 = newX;
            y1 = newY;
            x2 = newX2;
            y2 = newY2;
            myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            colors.setARGB(255, 50, 50, 50);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            myCanvas.drawCircle(centerX2, centerY2, baseRadius, colors);


            colors.setARGB(255, 255, 100, 0);
            myCanvas.drawCircle(newX, newY, hatRadius, colors);
            myCanvas.drawCircle(newX2, newY2, hatRadius, colors);
           // if(bitmap!=null)
           // myCanvas.drawBitmap(bitmap, 50, 50, colors);

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

    int upPI = 0;
    int downPI = 0;
    boolean inTouch = false;

    float zeroX = 0;
    float zeroY = 0;
    public float curX = centerX;
    public float curY = centerY;
    int index1 = -1;
    int ID1 = -1;

    float zeroX2 = 0;
    float zeroY2 = 0;
    public float curX2 = centerX2;
    public float curY2 = centerY2;
    int index2 = -1;
    int ID2 = -1;

    public float[] getPosition() {

        pos[0] = (x1 - centerX) * 500 / baseRadius + 1500;
        pos[1] = (y1 - centerY) * 500 / baseRadius + 1500;
        pos[2] = (x2 - centerX2) * 500 / baseRadius + 1500;
        pos[3] = (y2 - centerY2) * 500 / baseRadius + 1500;

        return pos;

    }

    public boolean onTouch(View v, MotionEvent e) {

        if (v.equals(this)) {

            int actionMask = e.getActionMasked();

            int pointerIndex = e.getActionIndex();


            switch (actionMask) {
                case MotionEvent.ACTION_DOWN:
                    inTouch = true;
                    if ((float) Math.sqrt((Math.pow(e.getX(0) - centerX, 2)) + Math.pow(e.getY(0) - centerY, 2)) < baseRadius) {
                        index1 = 0;
                        ID1 = e.getPointerId(0);
                        zeroX = e.getX(0);
                        zeroY = e.getY(0);
                        curX = centerX;
                        curY = centerY;
                    } else if ((float) Math.sqrt((Math.pow(e.getX(0) - centerX2, 2)) + Math.pow(e.getY(0) - centerY2, 2)) < baseRadius) {
                        index2 = 0;
                        ID2 = e.getPointerId(0);
                        zeroX2 = e.getX(0);
                        zeroY2 = e.getY(0);
                        curX2 = centerX2;
                        curY2 = centerY2;
                    }
                case MotionEvent.ACTION_POINTER_DOWN:
                    downPI = pointerIndex;
                    if ((float) Math.sqrt((Math.pow(e.getX(downPI) - centerX, 2)) + Math.pow(e.getY(downPI) - centerY, 2)) < baseRadius) {
                        index1 = downPI;
                        ID1 = e.getPointerId(downPI);
                        zeroX = e.getX(downPI);
                        zeroY = e.getY(downPI);
                        curX = centerX;
                        curY = centerY;
                    } else if ((float) Math.sqrt((Math.pow(e.getX(downPI) - centerX2, 2)) + Math.pow(e.getY(downPI) - centerY2, 2)) < baseRadius) {
                        index2 = downPI;
                        ID2 = e.getPointerId(downPI);
                        zeroX2 = e.getX(downPI);
                        zeroY2 = e.getY(downPI);
                        curX2 = centerX2;
                        curY2 = centerY2;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    inTouch = false;
                    ID1 = -1;
                    index1 = -1;
                    ID2 = -1;
                    index2 = -1;
                    drawJoystick(centerX, centerY, centerX2, centerY2);

                case MotionEvent.ACTION_POINTER_UP:
                    upPI = pointerIndex;
                    if (index1 == upPI) {
                        index1 = -1;
                        ID1 = -1;
                        index2 = 0;
                        drawJoystick(centerX, centerY, curX2, curY2);
                    } else if (index2 == upPI) {
                        index2 = -1;
                        ID2 = -1;
                        index1 = 0;
                        drawJoystick(curX, curY, centerX2, centerY2);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:


                    if (index1 != -1 && index2 != -1) {
                        curX = -zeroX + e.getX(index1) + centerX;
                        curY = -zeroY + e.getY(index1) + centerY;
                        curX2 = -zeroX2 + e.getX(index2) + centerX2;
                        curY2 = -zeroY2 + e.getY(index2) + centerY2;

                        float displacement2 = (float) Math.sqrt((Math.pow(e.getX(index2) - zeroX2, 2)) + Math.pow(e.getY(index2) - zeroY2, 2));
                        if (displacement2 > baseRadius) {
                            float dis = (float) Math.sqrt((Math.pow(e.getX(index2) - centerX2, 2)) + Math.pow(e.getY(index2) - centerY2, 2));
                            float ratio = baseRadius / dis;
                            curX2 = centerX2 + (e.getX(index2) - centerX2) * ratio;
                            curY2 = centerY2 + (e.getY(index2) - centerY2) * ratio;

                        }

                        float displacement = (float) Math.sqrt((Math.pow(e.getX(index1) - zeroX, 2)) + Math.pow(e.getY(index1) - zeroY, 2));
                        if (displacement > baseRadius) {
                            float dis = (float) Math.sqrt((Math.pow(e.getX(index1) - centerX, 2)) + Math.pow(e.getY(index1) - centerY, 2));
                            float ratio = baseRadius / dis;
                            curX = centerX + (e.getX(index1) - centerX) * ratio;
                            curY = centerY + (e.getY(index1) - centerY) * ratio;

                        }
                        drawJoystick(curX, curY, curX2, curY2);
                    }
                    if (index1 == -1) {
                        index2 = 0;
                        curX2 = -zeroX2 + e.getX(index2) + centerX2;
                        curY2 = -zeroY2 + e.getY(index2) + centerY2;
                        float displacement2 = (float) Math.sqrt((Math.pow(e.getX(index2) - zeroX2, 2)) + Math.pow(e.getY(index2) - zeroY2, 2));


                        if (displacement2 > baseRadius) {
                            float dis = (float) Math.sqrt((Math.pow(e.getX(index2) - centerX2, 2)) + Math.pow(e.getY(index2) - centerY2, 2));
                            float ratio = baseRadius / dis;
                            float constrainedX = centerX2 + (e.getX(index2) - centerX2) * ratio;
                            float constrainedY = centerY2 + (e.getY(index2) - centerY2) * ratio;
                            drawJoystick(centerX, centerY, constrainedX, constrainedY);
                        } else drawJoystick(centerX, centerY, curX2, curY2);
                    }
                    if (index2 == -1) {
                        index1 = 0;
                        curX = -zeroX + e.getX(index1) + centerX;
                        curY = -zeroY + e.getY(index1) + centerY;

                        float displacement = (float) Math.sqrt((Math.pow(e.getX(index1) - zeroX, 2)) + Math.pow(e.getY(index1) - zeroY, 2));


                        if (displacement > baseRadius) {
                            float dis = (float) Math.sqrt((Math.pow(e.getX(index1) - centerX, 2)) + Math.pow(e.getY(index1) - centerY, 2));
                            float ratio = baseRadius / dis;
                            float constrainedX = centerX + (e.getX(index1) - centerX) * ratio;
                            float constrainedY = centerY + (e.getY(index1) - centerY) * ratio;
                            drawJoystick(constrainedX, constrainedY, centerX2, centerY2);
                        } else drawJoystick(curX, curY, centerX2, centerY2);
                    }
                    break;
            }

        }
        return true;
    }


    public interface JoystickListener {
        void onJoystickMoved(float xPercent, float yPercent, float x2Percent, float y2Percent, int id);

    }
}
