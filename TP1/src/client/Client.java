package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
//import message.*;

/**
 *
 * @author renato
 */
public class Client {

    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public Client() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 1313);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }
}
