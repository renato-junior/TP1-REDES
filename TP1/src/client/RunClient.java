/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author renato
 */
public class RunClient {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        if (args.length != 6) {
            System.out.println("Parâmetros insuficientes.");
            System.exit(0);
        }
        Client client = null;
        client = new Client(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[0], Integer.parseInt(args[4]), Double.parseDouble(args[5]));
        client.runClient();
    }
}
