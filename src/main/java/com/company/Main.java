package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Client client = new Client("224.0.0.1");
        String req = client.work("Hello");
        while (!req.equals("end")) {
            req = client.work("Hello");
            System.out.println(req);
        }
        client.close();
    }
}