package com.company;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Client client = new Client(args[0]);
        try {
            while (true) {
                client.work();
                if (System.in.available() > 0) {
                    Scanner scanner = new Scanner(System.in);
                    String s = scanner.nextLine();
                    if (s.equals("stop")) break;
                }
            }
        } finally {
            client.close();
        }
    }
}