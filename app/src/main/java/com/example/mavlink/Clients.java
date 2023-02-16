package com.example.mavlink;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.IpSecManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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

    private String serverIP;
    private int port;
    private PipedInputStream MavInStream = new PipedInputStream(1024);
    private OutputStream MavOutStream;
    Pictures mPictures;


    Joystick joystick;


    public Clients(String address, int port, Joystick joystick) {
        this.serverIP = address;
        this.port = port;
        this.joystick = joystick;

    }


    public MavlinkConnection connection = null;
    public DatagramSocket MavSocket = null;
    private final int BUFFER_SIZE = 512;
    DatagramSocket udpSocket = null;
    public Socket socket=new Socket();

    public void run() {

        try {

            InetSocketAddress address = new InetSocketAddress(serverIP, port);
            InetSocketAddress UDPaddress = new InetSocketAddress(serverIP, 8001);
            Log.d("TCP_Client", "Connecting " + address);
            socket = new Socket();

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
            udpSocket = new DatagramSocket(socket.getLocalPort());
            Log.d("UDP_Client", "Connecting " + UDPaddress);


            PipedOutputStream out = new PipedOutputStream(MavInStream);
            MavSocket = new DatagramSocket();
            connection = MavlinkConnection.create(MavInStream, MavOutStream);
            HeartBeatMes();
            MavMes();
            rcChannelsOut();
            mPictures = new Pictures(udpSocket);
            mPictures.start();
            getContent();

            byte[] buf = new byte[BUFFER_SIZE];
            Log.d("TCP_Client", "Connected");

            while (!MavSocket.isClosed()) {
                DatagramPacket packet = new DatagramPacket(buf, BUFFER_SIZE);

                MavSocket.receive(packet);

                out.write(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            serverIP=MainActivity.curIP;
            try {
                Thread.sleep(1500);
            }
            catch (InterruptedException err)
            {
                err.printStackTrace();
            }
            run();
        } finally {
            if (MavSocket != null)
                MavSocket.close();
        }

    }

    static float[] position = new float[3];

    private void MavMes() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        MavlinkMessage message;
                        while (true) {
                            if (MavInStream.available() < 300) {
                                wait(100);
                                continue;
                            }
                            message = connection.next();

                            if (message instanceof Mavlink2Message) {
                                //Log.d("new_message", "Mav2");
                                Mavlink2Message message2 = (Mavlink2Message) message;
                                if (message.getPayload() instanceof LocalPositionNed) {
                                    MavlinkMessage<LocalPositionNed> localPos = (MavlinkMessage<LocalPositionNed>) message;
                                    position[0] = localPos.getPayload().x();
                                    position[1] = localPos.getPayload().y();
                                    position[2] = localPos.getPayload().z();
                                    String s = "position: " + "x: " + position[0] + " y: " + position[1] + " z: " + position[2];
                                    MainActivity.UpdatePosition(s);


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


    private void HeartBeatMes() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
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
                synchronized (this) {
                    try {

                        while (!MavSocket.isClosed()) {
                            int systemId = 255;
                            int componentId = 0;

                            float[] pos = joystick.getPosition();
                            RcChannelsOverride message = RcChannelsOverride.builder()
                                    .chan1Raw((int) pos[1])
                                    .chan2Raw((int) pos[0])
                                    .chan3Raw((int) pos[3])
                                    .chan4Raw((int) pos[2])
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

    BufferedReader reader;
    InputStream stream;
    HttpURLConnection HTTPconnection;
    static String version = "";

    private void getContent() {
        reader = null;
        stream = null;
        HTTPconnection = null;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        URL url = new URL("http://" + serverIP + "/info");
                        HTTPconnection = (HttpURLConnection) url.openConnection();
                        HTTPconnection.setRequestMethod("GET");
                        HTTPconnection.setReadTimeout(10000);
                        HTTPconnection.connect();
                        stream = HTTPconnection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(stream));
                        StringBuilder buf = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buf.append(line).append("\n");
                        }
                        String s = buf.toString();
                        version = "version: " + s;

                    } catch (IOException i) {
                        Log.d("Прошивка ", i.toString());

                    }


                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static String getVersion() {
        return version;
    }
}





