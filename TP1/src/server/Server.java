package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.AckPacket;
import message.MessagePacket;
import util.FileUtils;
import window.ServerSlidingWindow;

/**
 *
 * @author renato
 */
public class Server {

    private DatagramSocket socket;
    private FileUtils outputfile;
    private int windowSize;
    private double pError;

    private HashMap<ClientIdentifier, ServerSlidingWindow> clientsWindows;

    public Server(String fileName, int port, int windowSize, double pError) {
        try {
            socket = new DatagramSocket(port); // Cria o novo socket
        } catch (SocketException ex) {
            System.err.println("Um erro ocorreu: " + ex.getLocalizedMessage());
            System.exit(0);
        }
        try {
            this.outputfile = new FileUtils(fileName, true);
        } catch (FileNotFoundException ex) {
            System.err.println("Um erro ocorreu: " + ex.getLocalizedMessage());
            System.exit(0);
        }
        this.windowSize = windowSize;
        this.pError = pError;
        this.clientsWindows = new HashMap<>();
    }

    public void runServer() throws IOException {
        while (true) {
            // Cria o buffer e o pacote
            byte[] buffer = new byte[(int) MessagePacket.MAX_MESSAGE_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Recebe o pacote
            socket.receive(packet);

            // Identifica o cliente
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            ClientIdentifier client = new ClientIdentifier(address, port);

            verifyIfClientIsInList(client);
            
            // Trata o conteúdo do pacote
            MessagePacket mp = new MessagePacket(buffer);
            try {
                this.clientsWindows.get(client).addMessage(mp);
                sendAckMessage(client, mp);
            } catch (IllegalArgumentException | NoSuchAlgorithmException ex) {

            }
            List<MessagePacket> messagesToWrite = this.clientsWindows.get(client).slideWindow();
            for(MessagePacket m : messagesToWrite) {
                this.outputfile.writeLine(m.getMessage());
            }
        }
    }

    /**
     * Verifica se o cliente já tem uma janela alocada. Senão aloca uma janela
     * para o cliente.
     *
     * @param ci o identificador do cliente.
     */
    private void verifyIfClientIsInList(ClientIdentifier ci) {
        if (!clientsWindows.containsKey(ci)) {
            clientsWindows.put(ci, new ServerSlidingWindow(windowSize));
        }
    }

    private void sendAckMessage(ClientIdentifier client, MessagePacket mp) throws NoSuchAlgorithmException, IOException {
        AckPacket ack = new AckPacket(mp.getSeqNumber());
        byte[] buf = ack.buildAckBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, client.getAddress(), client.getPort());

        socket.send(packet); // Envia o pacote o Ack
    }

}
