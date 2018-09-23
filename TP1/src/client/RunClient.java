/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

/**
 *
 * @author renato
 */
public class RunClient {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        if (args.length != 5) {
            System.out.println("Parâmetros insuficientes.");
            System.exit(0);
        }
        Client client = null;
        StringTokenizer ipPort = new StringTokenizer(args[1], ":");
        String address = ipPort.nextToken();
        int port = Integer.parseInt(ipPort.nextToken());
        client = new Client(address, port, Integer.parseInt(args[2]), args[0], Integer.parseInt(args[3]), Double.parseDouble(args[4]));
        client.runClient();
    }
}
