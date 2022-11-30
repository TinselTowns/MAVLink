package com.example.mavlink;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.IpSecManager;
import android.os.Bundle;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.DataTransmissionHandshake;
import io.dronefleet.mavlink.common.EncapsulatedData;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.LocalPositionNed;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;
import io.dronefleet.mavlink.common.MavlinkDataStreamType;
import io.dronefleet.mavlink.common.RcChannelsOverride;

public class Clients extends Thread {

    private String serverIP ;
    private int port;
    private PipedInputStream MavInStream = new PipedInputStream(1024);
    private OutputStream MavOutStream;




    Joystick joystick;



    public Clients(String address,int port, Joystick joystick) {
        this.serverIP=address;
        this.port=port;
        this.joystick=joystick;

    }



    public MavlinkConnection connection=null;
    public DatagramSocket MavSocket = null;
    private final int BUFFER_SIZE = 512;
    DatagramSocket udpSocket=null;
   // FileOutputStream fos=null;
    //BufferedOutputStream out =null;
    public void run() {
        try {
           // f.createNewFile();
            //fos=new FileOutputStream(f);
            //out=new BufferedOutputStream(fos);
            InetSocketAddress address = new InetSocketAddress(serverIP, port);
            InetSocketAddress UDPaddress = new InetSocketAddress(serverIP, 8001);
            Log.d("TCP_Client", "Connecting " + address);
            Socket socket = new Socket();
            socket.connect(address, 10000);
            Log.d("TCP_Client", "Connect");
            MavOutStream = new OutputStream() {
                byte[] buffer;

                @Override
                public void write(int i) throws IOException {
                    write(new byte[]{(byte) i});
                }

                @Override
                public void write(byte[] buf) throws IOException {
                    buffer = buf;
                    flush();
                }

                @Override
                public synchronized void flush() throws IOException {
                  // Log.d("TCP", "length = " + buffer.length + " data = " + Arrays.toString(buffer));
                    MavSocket.send(new DatagramPacket(buffer, 0, buffer.length, UDPaddress));
                }
            };
            udpSocket=new DatagramSocket();
            Log.d("UDP_Client", "Connecting " + UDPaddress);
            udpSocket.connect(UDPaddress);
            byte[] buf2 = ("FILES").getBytes();
            DatagramPacket packet2 = new DatagramPacket(buf2, buf2.length,UDPaddress);
            udpSocket.send(packet2);




            Log.d("UDP_Client", "Connect");
            PipedOutputStream out = new PipedOutputStream(MavInStream);
            MavSocket = new DatagramSocket();
            connection = MavlinkConnection.create(MavInStream, MavOutStream);
            HeartBeatMes();
            MavMes();
            rcChannelsOut();
            UDPin();

            byte[] buf = new byte[BUFFER_SIZE];
            Log.d("TCP_Client", "Connected");

            while (!MavSocket.isClosed()) {
                DatagramPacket packet = new DatagramPacket(buf, BUFFER_SIZE);

                MavSocket.receive(packet);
                //Log.d("TCP_Client", "Received");
                out.write(Arrays.copyOfRange(packet.getData(),0, packet.getLength()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(MavSocket!=null)
            MavSocket.close();
        }

    }
float[] position=new float[3];

    private void MavMes() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    try {
                        MavlinkMessage message;
                        while (true) {
                            if (MavInStream.available() < 300){
                                wait(100);
                                continue;
                            }
                            message = connection.next();

                            if (message instanceof Mavlink2Message) {
                                //Log.d("new_message", "Mav2");
                                Mavlink2Message message2 = (Mavlink2Message) message;
                                if(message.getPayload() instanceof LocalPositionNed){
                                   // Log.d("new_message", "position");
                                    MavlinkMessage<LocalPositionNed> localPos = (MavlinkMessage<LocalPositionNed>) message;
                                    position[0]=localPos.getPayload().x();
                                    position[1]=localPos.getPayload().y();
                                    position[2]=localPos.getPayload().z();
                                    for(int i=0;i<3;i++)
                                    {
                                       //Log.d("new_message ", " "+position[i]);
                                    }

                                }


                                if (message.getPayload() instanceof Heartbeat) {
                                    //Log.d("new_message", "heart");
                                    MavlinkMessage<Heartbeat> heartbeatMessage = (MavlinkMessage<Heartbeat>) message;
                                }

                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    public static Bitmap bitmap=null;
    private void UDPin() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        byte[] message = new byte[2097152];
                        DatagramPacket packet = new DatagramPacket(message, message.length);
                        udpSocket.receive(packet);
                        String text = new String(message, 0, packet.getLength());

                        Log.d("Received data", " "+message[0]+" "+message[1]);
                        bitmap=BitmapFactory.decodeByteArray(message, 0, message.length);
                    } catch (IOException  e) {
                        e.printStackTrace();
                    }

                }
            }



        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public static  Bitmap paintBitmap()
    {
        return bitmap;
    }



    private void HeartBeatMes() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    try {
                        while (!MavSocket.isClosed()) {
                            int systemId = 255;
                            int componentId = 0;
                            Heartbeat heartbeat = Heartbeat.builder()
                                    .type(MavType.MAV_TYPE_GCS)
                                    .autopilot(MavAutopilot.MAV_AUTOPILOT_INVALID)
                                    .systemStatus(MavState.MAV_STATE_UNINIT)
                                    .mavlinkVersion(3)
                                    .build();
                            connection.send2(systemId, componentId, heartbeat);

                            wait(1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("TCP_out", e.toString());
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void rcChannelsOut() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    try {

                        while (!MavSocket.isClosed()) {
                            int systemId = 255;
                            int componentId = 0;

                            float[] pos=joystick.getPosition();
                            RcChannelsOverride message = RcChannelsOverride.builder()
                                    .chan1Raw((int)pos[1])
                                    .chan2Raw((int)pos[0])
                                    .chan3Raw((int)pos[3])
                                    .chan4Raw((int)pos[2])
                                    .build();
                            connection.send2(systemId, componentId, message);

                            wait(1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("TCP_out", e.toString());
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }



}


