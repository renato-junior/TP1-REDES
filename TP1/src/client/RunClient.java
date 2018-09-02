/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author renato
 */
public class RunClient {
    public static void main(String[] args) throws IOException {
        Client client = null;
        try {
            client = new Client();
        } catch (SocketException ex) {
            System.err.println("Erro ao criar o cliente: "+ex.getLocalizedMessage());
            System.exit(0);
        } catch (UnknownHostException ex) {
            System.err.println("Erro ao criar o cliente: "+ex.getLocalizedMessage());
            System.exit(0);
        }
        
        System.out.println(client.sendEcho("Ola"));
        System.out.println(client.sendEcho("eco"));
        System.out.println(client.sendEcho("end"));
    }
}
