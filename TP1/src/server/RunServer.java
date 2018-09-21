/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;

/**
 *
 * @author renato
 */
public class RunServer {
    public static void main(String[] args) throws IOException {
        if(args.length != 4){
            System.out.println("Parâmetros insuficientes.");
            System.exit(0);
        }
        Server server = new Server(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Double.parseDouble(args[3]));
        server.runServer();
    }
}
