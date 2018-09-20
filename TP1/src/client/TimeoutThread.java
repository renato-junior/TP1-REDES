package client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.MessagePacket;

/**
 *
 * @author renato
 */
public class TimeoutThread extends Thread {

    private Client client;

    public TimeoutThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            // Verifica o timeout da mensagem
            MessagePacket mp = client.getClientWindow().verifyTimeout(client.getTimeout());
            if (mp != null) {
                try {
                    // TODO verificar concorrência
                    client.sendMessage(mp);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(TimeoutThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TimeoutThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
