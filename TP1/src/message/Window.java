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
    private Boolean[] message_state;    // lista de confirmação de mensagens
    private int rws;                    // tamanho da janela
    private int lfa;                    // ultimo quadro aceitavel (last frame accepted)
    private int nfe;                    // proximo quadro aceitavel (next frame accepted)

    public Window(int rws){
        this.message_list = new Message[rws];
        this.message_state = new Boolean[rws];
        this.rws = rws;
        this.lfa = 0;
        this.nfe = 0;
    }

    public Message get_next_message(){
        return this.message_list[nfe];
    }
    
    public int get_next_message_index(){
        return this.nfe;
    }
    
    public int get_last_message_index(){
        return this.lfa;
    }
    
    public int get_window_size(){
        return this.rws;
    }

    public Boolen is_empty(){
        if(lfa == nfe){
            return true;
        }
        else
            return false;
    }

    public Boolean is_full(){
        if( (lfa + 1)%rws == nfe)
            return true;
        else
            return false;
    }

    public void receive_message(Message received_message){
        this.lfa = (this.lfa + 1)%this.rws;
        this.message_list[lfa] = received_message;
        this.message_state[lfa] = false;
    }

    public void confirm_message(long message_seq_number){
        for(int i = this.nfe; i != this.lfa; i = (i+1)%this.rws){
            if(this.message_list[i].get_sequential_number() == message_seq_number){
                this.message_state[i] = true;
            }
            if(this.message_state[i] && i == nfe){
                this.nfe = (this.nfe + 1)%this.rws;
            }
        }
}