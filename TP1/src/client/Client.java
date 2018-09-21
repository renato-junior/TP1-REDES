package client;

import util.FileUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.AckPacket;
import message.MessagePacket;
import window.ClientSlidingWindow;
//import message.*;
/**
 *
 * @author renato
 */
public class Client {

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private ClientSlidingWindow clientWindow;
    private FileUtils file;
    private int timeout;
    private double pError;

    private long seqNumber;

    private int uniqueLogMessagesSent;
    private int totalLogMessagesSent;
    private int totalIncorrectMessagesSent;

    public Client(String serverAddress, int serverPort, int windowSize, String fileName, int timeout, double pError) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
        this.clientWindow = new ClientSlidingWindow(windowSize);
        try {
            this.file = new FileUtils(fileName);
        } catch (FileNotFoundException ex) {
            System.out.println("Impossível abrir o arquivo.");
            System.exit(0);
        }
        this.timeout = timeout;
        this.pError = pError;
        this.seqNumber = 1;

        this.uniqueLogMessagesSent = 0;
        this.totalLogMessagesSent = 0;
        this.totalIncorrectMessagesSent = 0;
    }

    public void runClient() throws IOException, NoSuchAlgorithmException {
        long startTime = System.currentTimeMillis();
        TimeoutThread timeoutThread = new TimeoutThread(this);
        timeoutThread.start();
        while (true) {
            while (!this.clientWindow.isFull()) { // Enquanto a janela do cliente não está cheia
                String s = this.file.getLine(); // Lê a string do arquivo e cria mensagem com ela
                if (s != null) {
                    this.uniqueLogMessagesSent++;
                    MessagePacket mp = new MessagePacket(getSeqNumber(), s);
                    this.getClientWindow().addMessage(mp); // Adiciona a mensagem na janela do cliente
                    sendMessage(mp); // Envia o pacote com a mensagem
                } else {
                    break;
                }
            }
            // Recebe os ACKs
            byte[] buf = new byte[36];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            AckPacket ack = new AckPacket(buf); // Cria o pacote do ack
            if (ack.checkAckMD5()) { // Verifica MD5
//                System.out.println("ACK OK " + ack.getSeqNumber());
                this.getClientWindow().confirmMessage(ack.getSeqNumber());
            } else {
//                System.out.println("ACK NÃO OK " + ack.getSeqNumber());
            }

            if (!this.file.hasNextLine() && this.getClientWindow().isEmpty()) { // Se não tem mais mensagens para ler do arquivo e a janela já está vazia, encerra o cliente
                break;
            }
        }
        this.close();
        long totalExecutionTime = System.currentTimeMillis() - startTime;
        System.out.println(this.uniqueLogMessagesSent + " " + this.totalLogMessagesSent + " " + this.totalIncorrectMessagesSent + " " + ((double) totalExecutionTime / 1000) + "s");
        System.exit(0);
    }

    public void close() {
        socket.close();
    }

    public void sendMessage(MessagePacket mp) throws NoSuchAlgorithmException, IOException {
        byte[] buf = mp.buildMessageBytes(!sendMessageWithError());
        DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, serverPort);

        socket.send(packet); // Envia o pacote com a mensagem

        this.totalLogMessagesSent++;
    }
    
    private boolean sendMessageWithError() {
        return Math.random() < this.pError;
    }

    private long getSeqNumber() {
        long sn = this.seqNumber;
        this.seqNumber++;
        return sn;
    }

    public ClientSlidingWindow getClientWindow() {
        return clientWindow;
    }

    public int getTimeout() {
        return timeout;
    }
}
