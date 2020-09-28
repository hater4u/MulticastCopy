package com.company;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Client extends Thread{
    private InetAddress address;
    private MulticastSocket socket;
    private SocketAddress socketAddress;
    private Map<Node, Long> copiesMap = new ConcurrentHashMap<>(); //HashMap<>();
    private UUID uuid = UUID.randomUUID();

    private byte[] buf = new byte[512];

    public Client(String addr) {
        int port = 4445;
        try {
            socket = new MulticastSocket(port);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            socket.setSoTimeout(1000);
            address = InetAddress.getByName(addr);
            if (address.isMulticastAddress()) {
                socketAddress = new InetSocketAddress(address, port);
                socket.joinGroup(socketAddress, NetworkInterface.getByInetAddress(address));
            } else {
                System.out.println("Argument is not multicast address");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            socket.close();
        }
    }

    public void work() {
        long startTimeout = System.currentTimeMillis();
        boolean flag;
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
                InetSocketAddress receivedAddress = new InetSocketAddress(packet.getAddress(), packet.getPort());
                Node node = new Node(receivedUuid, receivedAddress);
                if (copiesMap.containsKey(node)) {
                    copiesMap.replace(node, System.currentTimeMillis());
                } else {
                    copiesMap.put(node, System.currentTimeMillis());
                    for (Map.Entry<Node, Long> entry: copiesMap.entrySet()) {
                        System.out.print(entry.getKey().getNodeAddress() + ", ");
                    }
                    System.out.println();
                }
            }
            flag = false;
            for (Map.Entry<Node, Long> entry: copiesMap.entrySet()) {
                if (System.currentTimeMillis() - entry.getValue() > 5000) {
                    copiesMap.remove(entry.getKey());
                    flag = true;
                }
            }
            if (flag) {
                for (Map.Entry<Node, Long> entry: copiesMap.entrySet()) {
                    System.out.print(entry.getKey().getNodeAddress()  + ", ");
                }
                System.out.println();
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