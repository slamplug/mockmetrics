package com.firstutility.mockmetrics.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class UDPClient {

    private String host;
    private int port;

    public UDPClient(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public UDPClient(final int port) {
        this("localhost", port);
    }

    public void send(final String payload) throws Exception {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] data = payload.getBytes();
            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName(host),
                    port
            );
            System.out.println("sending packet " + packet.getAddress().getHostAddress());
            socket.send(packet);

        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    public void sendMetricWithPauseAfter(final String metric) throws Exception {
        this.send(metric);
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
        }
    }

    public static void main(String[] args) throws Exception {
        UDPClient udpClient = new UDPClient(9999);
        udpClient.send("test1:1|c\ntest2:1|c");
    }
}
