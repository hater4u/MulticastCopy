package com.company;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Client client = new Client(args[0]);
        while (true) {
            client.work();
            if(System.in.available() != -1) {
                Scanner scanner = new Scanner(System.in);
                String s = scanner.nextLine();
                if(s.equals("stop")) break;
            }
        }
        client.close();
    }
}