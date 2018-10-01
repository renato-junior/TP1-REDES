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
import message.AckPacket;
import message.MessagePacket;
import window.ClientSlidingWindow;

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
    private final int timeout;
    private final double pError;

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
        this.seqNumber = 0;

        this.uniqueLogMessagesSent = 0;
        this.totalLogMessagesSent = 0;
        this.totalIncorrectMessagesSent = 0;
    }

    /**
     * Executa o cliente. O cliente irá ler o arquivo passado como parâmetro
     * para o construtor, e para cada linha, cria um MessagePacket e o envia
     * para o servidor. Ele também irá receber as confirmações do servidor e
     * controlar o timeout dos pacotes enviados.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
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

    /**
     * Envia a mensagem para o servidor. Essa mensagem pode ser enviada com erro
     * no MD5, com base no método sendMessageWithError().
     *
     * @param mp o pacote da mensagem a ser enviada.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public void sendMessage(MessagePacket mp) throws NoSuchAlgorithmException, IOException {
        boolean msgError = sendMessageWithError();
        byte[] buf = mp.buildMessageBytes(!msgError);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, serverPort);

        socket.send(packet); // Envia o pacote com a mensagem

        if (msgError == true) {
            this.totalIncorrectMessagesSent++;
        }
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
