package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author renato
 */
public class Server {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public Server() {
        try {
            socket = new DatagramSocket(1313); // Cria o novo socket
        } catch (SocketException ex) {
            System.err.println("Um erro ocorreu: "+ex.getLocalizedMessage());
        }
    }

    public void startServer() {
        running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());

                System.err.println(received);
                if (received.trim().equals("end")) { // Se recebe end, para
                    running = false;
                    continue;
                }
                socket.send(packet);
            } catch (IOException ex) {
                System.err.println("Um erro ocorreu: "+ex.getLocalizedMessage());
            }
        }
        
        socket.close();
    }

}
