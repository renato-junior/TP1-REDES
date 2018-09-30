package window;

import java.util.ArrayList;
import java.util.List;
import message.MessagePacket;

public class ServerSlidingWindow {

    private final MessagePacket[] messageList;        // lista de mensagens na janela
    private final int windowSize;                     // tamanho da janela
    private int firstMessage;                  // seqNumber na primeira mensagem na janela

    public ServerSlidingWindow(int windowSize) {
        this.windowSize = windowSize;
        this.firstMessage = 1;

        // Inicializa vetores
        this.messageList = new MessagePacket[this.windowSize];
        for (int i = 0; i < this.windowSize; i++) {
            this.messageList[i] = null;
        }
    }

    /**
     * Adiciona a mensagem na janela.
     *
     * @param mp a mensagem a ser adicionada.
     * @throws IllegalArgumentException
     */
    public synchronized void addMessage(MessagePacket mp) {
        int pos = (int) mp.getSeqNumber() - this.firstMessage;
        if (pos >= this.windowSize) {
            throw new IllegalArgumentException("Impossível adicionar mensagem na janela");
        }
        this.messageList[pos] = mp;
    }

    /**
     * Desliza a janela deslizante, tirando todos os primeiros pacotes que foram
     * confirmado.
     *
     * @return a lista de mensagens ordernadas para serem escritas no arquivo de
     * saída.
     */
    public synchronized List<MessagePacket> slideWindow() {
        List<MessagePacket> messages = new ArrayList<>();
        int maxPos = -1;
        for (int i = 0; i < this.windowSize; i++) { // Verifica até onde pode deslizar a janela
            if (this.messageList[i] != null) {
                maxPos = i;
            } else {
                break;
            }
        }
        if (maxPos != -1) {
            int lastConfirmedMessage = (int) this.messageList[maxPos].getSeqNumber();
            // Desliza a janela
            for (int i = 0; i < maxPos + 1; i++) {
                messages.add(this.messageList[i]);
                this.messageList[i] = null;
            }
            for (int i = maxPos + 1; i < this.windowSize; i++) {
                this.messageList[i - maxPos - 1] = this.messageList[i];
                this.messageList[i] = null;
            }
            // Verifica a nova primeira mensagem
            this.firstMessage = lastConfirmedMessage + 1;
        }
        return messages;
    }

    /**
     * Verifica se a janela está cheia.
     *
     * @return true se a janela está cheia. False caso contrário.
     */
    public synchronized boolean isFull() {
        return messageList[windowSize - 1] != null;
    }

    /**
     * Verifica se a janela está vazia.
     *
     * @return true se a janela está vazia. False caso contrário.
     */
    public synchronized boolean isEmpty() {
        for (MessagePacket mp : this.messageList) {
            if (mp != null) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean isMessageValid(MessagePacket mp) {
        if (mp.getSeqNumber() >= this.firstMessage && mp.getSeqNumber() < this.firstMessage + this.windowSize) {
            return true;
        } else {
            return false;
        }
    }

    public int getFirstMessage() {
        return firstMessage;
    }

}
