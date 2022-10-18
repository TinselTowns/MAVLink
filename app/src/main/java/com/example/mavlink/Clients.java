package com.example.mavlink;

import android.util.Log;


import java.io.EOFException;
import java.io.IOException;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
    //private PipedOutputStream MavOutStream=new PipedOutputStream(MavInStream);

    public Clients(String address,int port) {
        this.serverIP=address;
        this.port=port;

    }



    public MavlinkConnection connection=null;
    public Socket MavSocket = null;

    public void run() {
        try {
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            Socket socket = new Socket(serverAddr, port);
            Log.d("TCP","connected");

            try {
                MavSocket = new Socket(serverIP, 8001);
                connection = MavlinkConnection.create(
                        MavSocket.getInputStream(),
                        MavSocket.getOutputStream());
                HeartBeatMes();
                MavMes();

            } catch (EOFException eof) {

            } catch (IOException io) {

            } finally {
                socket.close();
            }
        } catch (Exception e) {
        }

    }

    private void MavMes() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    MavlinkMessage message;
                    while ((message = connection.next()) != null) {

                        if (message instanceof Mavlink2Message) {

                            Mavlink2Message message2 = (Mavlink2Message) message;

                            if (message.getPayload() instanceof Heartbeat) {

                                MavlinkMessage<Heartbeat> heartbeatMessage = (MavlinkMessage<Heartbeat>) message;
                            }
                        }

                    }
                } catch (IOException io) {
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

                    }
                } catch (IOException io) {
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}



