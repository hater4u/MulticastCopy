package com.company;

import java.net.InetSocketAddress;
import java.util.UUID;

public class Node {
    private UUID uuid;
    private InetSocketAddress address;

    public Node(UUID receivedUuid, InetSocketAddress socketAddress) {
        uuid = receivedUuid;
        address = socketAddress;
    }

    public InetSocketAddress getNodeAddress() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Node node = (Node) obj;
        return node.uuid.equals(this.uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }
}
