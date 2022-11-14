package com.example.mavlink;

import android.util.Log;


import java.io.EOFException;
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
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;

public class Clients extends Thread {

    private String serverIP ;
    private int port;
    private PipedInputStream MavInStream = new PipedInputStream(1024);
    private OutputStream MavOutStream;

    public Clients(String address,int port) {
        this.serverIP=address;
        this.port=port;

    }



    public MavlinkConnection connection=null;
    public DatagramSocket MavSocket = null;
    private final int BUFFER_SIZE = 512;
    public void run() {
        try {
            InetSocketAddress address = new InetSocketAddress(serverIP, port);
            InetSocketAddress UDPaddress = new InetSocketAddress(serverIP, 8001);
            Log.d("TCP_Client", "Connecting " + address);
            Socket socket = new Socket();
            socket.connect(address, 10000);
            Log.d("TCP_Client", "Connect");
            MavOutStream = new OutputStream() { // создание потока отправки
                byte[] buffer;

                @Override
                public void write(int i) throws IOException {
                    write(new byte[]{(byte) i});
                }

                @Override
                public void write(byte[] buf) throws IOException {
                    buffer = buf;
                    flush(); // сообщения отправляются сразу
                }

                @Override
                public synchronized void flush() throws IOException {
                    Log.d("TCP", "length = " + buffer.length + " data = " + Arrays.toString(buffer));
                    MavSocket.send(new DatagramPacket(buffer, 0, buffer.length, UDPaddress));
                }
            };
            PipedOutputStream out = new PipedOutputStream(MavInStream);
            MavSocket = new DatagramSocket();
            connection = MavlinkConnection.create(MavInStream, MavOutStream);
            HeartBeatMes();
            MavMes();
            byte[] buf = new byte[BUFFER_SIZE];
            Log.d("TCP_Client", "Connected");
            while (!MavSocket.isClosed()) {
                DatagramPacket packet = new DatagramPacket(buf, BUFFER_SIZE);
                Log.d("TCP_Client", "Wait");
                MavSocket.receive(packet);
                Log.d("TCP_Client", "Received");
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
                                Log.d("new_message", "Mav2");
                                Mavlink2Message message2 = (Mavlink2Message) message;

                                if (message.getPayload() instanceof Heartbeat) {
                                    Log.d("new_message", "heart");
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
                            Log.d("TCP_out", "HB");
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


