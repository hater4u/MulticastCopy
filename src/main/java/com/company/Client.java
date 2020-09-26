package com.company;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Client extends Thread{
    private InetAddress address;
    private MulticastSocket socket;
    private SocketAddress socketAddress;
    private Map<UUID, Long> copiesMap = new HashMap<>();
    private UUID uuid = UUID.randomUUID();

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
                System.out.println("Argument is not multicast address");
            }
        } catch (SocketException e) {
            System.out.println("Some error: " + e);
        } catch (IOException e) {
            System.out.println("Can't open socket: " + e);
        }
    }

    public void work() {
        long startTimeout = System.currentTimeMillis();
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (System.currentTimeMillis() - startTimeout < 1000) {
            packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
//                System.out.println("error :" + e);
            }
            if (packet.getAddress() != null) {
                UUID receivedUuid = UUID.fromString(new String(packet.getData(), 0, packet.getLength()));
                if (copiesMap.containsKey(receivedUuid)) {
                    copiesMap.replace(receivedUuid, System.currentTimeMillis());
                } else {
                    copiesMap.put(receivedUuid, System.currentTimeMillis());
                    System.out.println(packet.getAddress());
                }
            }
            for (Map.Entry<UUID, Long> entry: copiesMap.entrySet()) {
                if (System.currentTimeMillis() - entry.getValue() > 5000) {
                    copiesMap.remove(entry.getKey());
                    System.out.println(packet.getAddress());
                }
            }
        }
        buf = uuid.toString().getBytes();

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