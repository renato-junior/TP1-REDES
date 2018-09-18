package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import message.AckPacket;
import message.MessagePacket;
import window.SlidingWindow;
//import message.*;

/**
 *
 * @author renato
 */
public class Client {

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private SlidingWindow clientWindow;
    private FileUtils file;
    private int timeout;
    private double pError;

    private long seqNumber;

    public Client(String serverAddress, int serverPort, int windowSize, String fileName, int timeout, double pError) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
        this.clientWindow = new SlidingWindow(windowSize);
        try {
            this.file = new FileUtils(fileName);
        } catch (FileNotFoundException ex) {
            System.out.println("Impossível abrir o arquivo.");
            System.exit(0);
        }
        this.timeout = timeout;
        this.pError = pError;
        this.seqNumber = 1;

//        this.socket.setSoTimeout(3000);
    }

    public void runClient() throws IOException, NoSuchAlgorithmException {
        TimeoutThread timeoutThread = new TimeoutThread(this);
        timeoutThread.start();
        while (true) {
            while (!this.clientWindow.isFull()) { // Enquanto a janela do cliente não está cheia
                String s = this.file.getLine(); // Lê a string do arquivo e cria mensagem com ela
                if (s != null) {
                    System.out.println("Mensagem = " + s);
                    MessagePacket mp = new MessagePacket(getSeqNumber(), s);
                    this.getClientWindow().addMessage(mp); // Adiciona a mensagem na janela do cliente
                    enviaMensagem(mp); // Envia o pacote com a mensagem
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
                System.out.println("ACK OK " + ack.getSeqNumber());
                this.getClientWindow().confirmMessage(ack.getSeqNumber());
            } else {
                System.out.println("ACK NÃO OK " + ack.getSeqNumber());
            }

            if (this.getClientWindow().isEmpty()) { // Se não tem mais mensagens para ler do arquivo e a janela já está vazia, encerra o cliente
                break;
            }
        }
        this.close();
        System.exit(0);
    }

    public void close() {
        socket.close();
    }

    public void enviaMensagem(MessagePacket mp) throws NoSuchAlgorithmException, IOException {
        byte[] buf = mp.buildMessageBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, serverPort);

        socket.send(packet); // Envia o pacote com a mensagem

    }

    private long getSeqNumber() {
        long sn = this.seqNumber;
        this.seqNumber++;
        return sn;
    }

    public SlidingWindow getClientWindow() {
        return clientWindow;
    }

    public int getTimeout() {
        return timeout;
    }
}
