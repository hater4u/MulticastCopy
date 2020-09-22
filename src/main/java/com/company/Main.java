package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Client client = new Client("224.0.0.1");
        while (true) {
            client.work("Hello");
            if(false) break;
        }
        client.close();
    }
}