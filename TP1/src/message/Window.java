package message;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import message.Message;

public class Window{
    private Message[] message_list;     // lista de mensagens na janela
    private int rws;                    // tamanho da janela
    private int lfa;                    // ultimo quadro aceitavel (last frame accepted)
    private int nfe;                    // proximo quadro aceitavel (next frame accepted)

    public Window(int rws){
        this.message_list = new Message[rws];
        this.rws = rws;
        this.lfa = rws - 1;
        this.nfe = 0;
    }

    public Message get_next_message(){
        return this.message_list[nfe];
    }
    public int get_next_message_index(){
        return this.nfe;
    }
    public void add_new_message(Message new_message){

    }
    public Boolean is_full(){
        return false;
    }

    public void message_received(Message received_message){

    }
}