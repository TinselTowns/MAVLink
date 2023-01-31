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
import android.widget.TextView;


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
    Bitmap currentBitmap = null;
    String position = "position: x:0.0 y:0.0 z:0.0";


    private void setUp() {

        centerX = Math.min(getWidth(), getHeight()) / 3;
        centerY = getHeight() - Math.min(getWidth(), getHeight()) / 3;
        centerX2 = getWidth() - Math.min(getWidth(), getHeight()) / 3;
        centerY2 = getHeight() - Math.min(getWidth(), getHeight()) / 3;
        baseRadius = 2*Math.min(getWidth(), getHeight()) / 7;
        hatRadius = Math.min(getWidth(), getHeight()) / 18;
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

    public void drawBitmap(Bitmap bitmap) {
        currentBitmap = bitmap;

        drawJoystick(curX, curY, curX2, curY2);


    }

    public void printPosition(String positions) {
        position = positions;
        drawJoystick(curX, curY, curX2, curY2);


    }

    public Canvas myCanvas = null;

    public void drawJoystick(float newX, float newY, float newX2, float newY2) {
        if (getHolder().getSurface().isValid()) {
            curX = x1 = newX;
            curY = y1 = newY;
            curX2 = x2 = newX2;
            curY2 = y2 = newY2;
            myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            colors.setARGB(255, 50, 50, 50);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            myCanvas.drawCircle(centerX2, centerY2, baseRadius, colors);


            colors.setARGB(255, 255, 100, 0);
            myCanvas.drawCircle(newX, newY, hatRadius, colors);
            myCanvas.drawCircle(newX2, newY2, hatRadius, colors);
            colors.setTextSize(40.0f);
            myCanvas.drawText(position, getWidth() / 30, getHeight() / 25, colors);
            myCanvas.drawText(Clients.getVersion(), 4 * getWidth() / 5, getHeight() / 25, colors);
            if (currentBitmap != null) {

                myCanvas.drawBitmap(currentBitmap, 3 * getWidth() / 7, getHeight() / 3, colors);
            }
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

                    zeroX = e.getX(0);
                    zeroY = e.getY(0);
                    if ((float) Math.sqrt((Math.pow(e.getX(0) - centerX, 2)) + Math.pow(e.getY(0) - centerY, 2)) < baseRadius) {
                        inTouch = true;
                        index1 = 0;
                        ID1 = e.getPointerId(0);
                        zeroX = e.getX(0);
                        zeroY = e.getY(0);
                        curX = centerX;
                        curY = centerY;
                    } else {
                        zeroX2 = e.getX(0);
                        zeroY2 = e.getY(0);
                        if ((float) Math.sqrt((Math.pow(e.getX(0) - centerX2, 2)) + Math.pow(e.getY(0) - centerY2, 2)) < baseRadius) {
                            index2 = 0;
                            inTouch = true;
                            ID2 = e.getPointerId(0);
                            zeroX2 = e.getX(0);
                            zeroY2 = e.getY(0);
                            curX2 = centerX2;
                            curY2 = centerY2;

                        }
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if(pointerIndex<2){
                        downPI = pointerIndex;
                        if ((float) Math.sqrt((Math.pow(e.getX(downPI) - centerX, 2)) + Math.pow(e.getY(downPI) - centerY, 2)) < baseRadius && index1==-1) {
                            index1 = downPI;
                            ID1 = e.getPointerId(downPI);
                            zeroX = e.getX(downPI);
                            zeroY = e.getY(downPI);
                            curX = centerX;
                            curY = centerY;


                        } else if ((float) Math.sqrt((Math.pow(e.getX(downPI) - centerX2, 2)) + Math.pow(e.getY(downPI) - centerY2, 2)) < baseRadius && index2==-1) {
                            index2 = downPI;
                            ID2 = e.getPointerId(downPI);
                            zeroX2 = e.getX(downPI);
                            zeroY2 = e.getY(downPI);
                            curX2 = centerX2;
                            curY2 = centerY2;

                        }}
                    break;

                case MotionEvent.ACTION_UP:
                    inTouch = false;
                    ID1 = -1;
                    index1 = -1;
                    ID2 = -1;
                    index2 = -1;
                    zeroX = centerX;
                    zeroY = centerY;
                    zeroX2 = centerX2;
                    zeroY2 = centerY2;
                    drawJoystick(centerX, centerY, centerX2, centerY2);

                case MotionEvent.ACTION_POINTER_UP:
                    upPI = pointerIndex;

                    if (index1 == upPI) {
                        index1 = -1;
                        ID1 = -1;
                        index2 = 0;
                        zeroX = centerX;
                        zeroY = centerY;
                        drawJoystick(centerX, centerY, curX2, curY2);
                    } else if (index2 == upPI) {
                        index2 = -1;
                        ID2 = -1;
                        index1 = 0;
                        zeroX2 = centerX2;
                        zeroY2 = centerY2;
                        drawJoystick(curX, curY, centerX2, centerY2);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:


                    if (index1 != -1 && index2 != -1) {
                        curX = e.getX(index1) ;
                        curY =e.getY(index1);
                        curX2 = e.getX(index2);
                        curY2 =e.getY(index2);


                        float displacement2 = (float) Math.sqrt((Math.pow(e.getX(index2) - centerX2, 2)) + Math.pow(e.getY(index2) - centerY2, 2));
                        if (displacement2 > baseRadius) {
                            float dis = (float) Math.sqrt((Math.pow(e.getX(index2) - centerX2, 2)) + Math.pow(e.getY(index2) - centerY2, 2));
                            float ratio = baseRadius / dis;
                            curX2 = centerX2 + (e.getX(index2) - centerX2) * ratio;
                            curY2 = centerY2 + (e.getY(index2) - centerY2) * ratio;

                        }

                        float displacement = (float) Math.sqrt((Math.pow(e.getX(index1) - centerX, 2)) + Math.pow(e.getY(index1) - centerY, 2));
                        if (displacement > baseRadius) {
                            float dis = (float) Math.sqrt((Math.pow(e.getX(index1) - centerX, 2)) + Math.pow(e.getY(index1) - centerY, 2));
                            float ratio = baseRadius / dis;
                            curX = centerX + (e.getX(index1) - centerX) * ratio;
                            curY = centerY + (e.getY(index1) - centerY) * ratio;

                        }
                        drawJoystick(curX, curY, curX2, curY2);
                    }

                    if (index2 == -1 && inTouch) {

                        index1 = 0;
                        curX = -zeroX + e.getX(index1) + centerX;
                        curY = -zeroY + e.getY(index1) + centerY;

                        float displacement = (float) Math.sqrt((Math.pow(e.getX(index1) - centerX, 2)) + Math.pow(e.getY(index1) - centerY, 2));


                        if (displacement > baseRadius) {
                            float dis = (float) Math.sqrt((Math.pow(e.getX(index1) - centerX, 2)) + Math.pow(e.getY(index1) - centerY, 2));
                            float ratio = baseRadius / dis;
                            float constrainedX = centerX + (e.getX(index1) - centerX) * ratio;
                            float constrainedY = centerY + (e.getY(index1) - centerY) * ratio;
                            drawJoystick(constrainedX, constrainedY, centerX2, centerY2);
                        } else {

                            drawJoystick(e.getX(index1), e.getY(index1), centerX2, centerY2);
                        }
                    }
                    if (index1 == -1 && inTouch) {

                        index2 = 0;
                        curX2 = -zeroX2 + e.getX(index2) + centerX2;
                        curY2 = -zeroY2 + e.getY(index2) + centerY2;
                        float displacement2 = (float) Math.sqrt((Math.pow(e.getX(index2) - centerX2, 2)) + Math.pow(e.getY(index2) - centerY2, 2));

                        if (displacement2 > baseRadius) {
                            float dis = (float) Math.sqrt((Math.pow(e.getX(index2) - centerX2, 2)) + Math.pow(e.getY(index2) - centerY2, 2));
                            float ratio = baseRadius / dis;
                            float constrainedX = centerX2 + (e.getX(index2) - centerX2) * ratio;
                            float constrainedY = centerY2 + (e.getY(index2) - centerY2) * ratio;
                            drawJoystick(centerX, centerY, constrainedX, constrainedY);
                        } else drawJoystick(centerX, centerY, e.getX(index2), e.getY(index2));
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
