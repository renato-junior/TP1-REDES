package message;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.*;

import com.sun.javafx.runtime.VersionInfo;

import java.math.*;

public class Message{
    private long seq_number;               // número de sequencia da mensagem (unsigned 64 bits )
    private int time_sent;                 // tempo desde o envio da mensagem (unsigned 32 bits)
    private long time_stamp;               // tempo desde a data referência do período corrente (unsigned 64 bits)
    private short message_size;            // tamanho da mensagem (16 bits )
    private String message;                // a string da mensagem (lenght < 2^14 bytes)
    private BigInteger verification_code;  // codigo de correção de erro (128 bits)

    Message(long seq_number,String message){
        this.seq_number = seq_number;
        this.message = message;
        this.message_size = message.length();
    }

    private void calculate_verification_error(){
        String verification_string = String(this.seq_number) + String(time_sent) + String(time_stamp);
        verification_string += String(message_size) + String(message) + String(verification_code);
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(s.getBytes(),0,s.lenght);
        this.verification_code = new BigInteger(1,m.digest());
    }

    private void get_message_times(){

    }

    public long get_sequential_number(){
        return this.seq_number;
    }
    public int get_time_sent(){
        return this.time_sent;
    }
    public long get_time_stamp(){
        return this.time_stamp;
    }
    public short get_message_size(){
        return this.message_size;
    }
    public String get_message(){
        return this.message;
    }
    public get_verification_code(){
        return this.verification_code;
    }
    public void on_send(){
        get_message_times();
        calculate_verification_error();
    }

}