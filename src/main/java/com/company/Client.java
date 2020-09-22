package com.company;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Client extends Thread{
    // private DatagramSocket socket;
    private InetAddress address;
    private MulticastSocket socket;
    private SocketAddress socketAddress;
    private Map<SocketAddress, Long> copiesMap = new HashMap<>();

    private byte[] buf = new byte[512];

    public Client(String addr) {
        int port = 4445;
        try {
            socket = new MulticastSocket(port);
            socket.setSoTimeout(1000);
            address = InetAddress.getByName(addr);
            if (address.isMulticastAddress()) {
                socketAddress = new InetSocketAddress(address, port);
                socket.joinGroup(socketAddress, NetworkInterface.getByInetAddress(address));
            } else {
                System.out.println("Fuck you");
            }
        } catch (SocketException e) {
            System.out.println("Some error: " + e);
        } catch (IOException e) {
            System.out.println("Can't open socket: " + e);
        }
    }

    public void work(String msg) {
        long startTimeout = System.currentTimeMillis();
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (System.currentTimeMillis() - startTimeout < 1000) {
            packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
//                System.out.println("error :" + e);
            }
            String received = new String(packet.getData(), 0, packet.getLength());
            if (packet.getAddress() != null) {
                SocketAddress item = new InetSocketAddress(packet.getAddress(), packet.getPort());
                if (copiesMap.containsKey(item)) {
                    copiesMap.replace(item, System.currentTimeMillis());
                } else {
                    copiesMap.put(item, System.currentTimeMillis());
                    System.out.println(copiesMap.toString());
                }
            }
            for (Map.Entry<SocketAddress, Long> entry: copiesMap.entrySet()) {
                if (System.currentTimeMillis() - entry.getValue() > 15000) {
                    copiesMap.remove(entry.getKey());
                    System.out.println(copiesMap.toString());
                }
            }
        }
        buf = msg.getBytes();
        packet = new DatagramPacket(buf, buf.length, socketAddress);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        socket.close();
    }
}