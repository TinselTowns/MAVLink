package com.example.mavlink;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Pictures extends Thread  {
    public static Bitmap bitmap = null;
    DatagramSocket udpSocket;

    public Pictures(DatagramSocket socket) {
        udpSocket=socket;

    }

    public void run() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        while (true) {
                            byte[] message = new byte[32000];
                            Log.d("Received bitmap", "wait");
                            DatagramPacket packet = new DatagramPacket(message, message.length);
                            udpSocket.receive(packet);
                            Log.d("Received bitmap", packet.toString());

                            bitmap = BitmapFactory.decodeByteArray(message, 0, message.length);
                        }
                    }
                    catch (IOException e) {
                        Log.d("Received data", e.toString());
                        ;
                    }

                }
            }

        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static Bitmap paintBitmap() {
        return bitmap;
    }
}
