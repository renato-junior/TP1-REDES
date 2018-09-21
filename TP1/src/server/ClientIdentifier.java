/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.InetAddress;
import java.util.Objects;

/**
 *
 * @author renato
 */
public class ClientIdentifier {

    private InetAddress address;
    private int port;

    public ClientIdentifier(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public String toString() {
        return address.getHostAddress() + Integer.toString(port);
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
