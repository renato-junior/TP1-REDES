package window;

import message.MessagePacket;

public class ClientSlidingWindow {

    private final MessagePacket[] messageList;        // lista de mensagens na janela
    private final boolean[] messageState;             // lista de confirmação de mensagens
    private final long[] timeoutCounter;               // Vetor para fazer o controle do timeout
    private final int windowSize;                     // tamanho da janela
    private int firstMessage;                  // seqNumber na primeira mensagem na janela

    public ClientSlidingWindow(int windowSize) {
        this.windowSize = windowSize;
        this.firstMessage = 0;

        // Inicializa vetores
        this.messageList = new MessagePacket[this.windowSize];
        this.messageState = new boolean[this.windowSize];
        this.timeoutCounter = new long[this.windowSize];
        for (int i = 0; i < this.windowSize; i++) {
            this.messageList[i] = null;
            this.messageState[i] = false;
            this.timeoutCounter[i] = 0;
        }
    }

    /**
     * Adiciona a mensagem na janela.
     *
     * @param mp a mensagem a ser adicionada.
     * @throws IllegalArgumentException
     */
    public synchronized void addMessage(MessagePacket mp) {
        if (this.isEmpty()) {
            this.firstMessage = (int) mp.getSeqNumber();
        }
        int pos = (int) mp.getSeqNumber() - this.firstMessage;
        if (pos >= this.windowSize) {
            throw new IllegalArgumentException("Impossível adicionar mensagem na janela");
        }
        this.messageList[pos] = mp;
        this.messageState[pos] = false;
        this.timeoutCounter[pos] = System.currentTimeMillis();
    }

    /**
     * Confirma a mensagem com o seqNum.
     *
     * @param msgSeqNum o seqNum da mensagem a ser confirmada.
     */
    public synchronized void confirmMessage(long msgSeqNum) {
        int pos = (int) msgSeqNum - this.firstMessage;
        if (pos >= 0 && pos < windowSize && this.messageList[pos].getSeqNumber() == msgSeqNum) {
            this.messageState[pos] = true;
            this.slideWindow();
        }
    }

    /**
     * Desliza a janela deslizante, tirando todos os primeiros pacotes que foram
     * confirmado.
     */
    private synchronized void slideWindow() {
        int maxPos = -1;
        for (int i = 0; i < this.windowSize; i++) { // Verifica até onde pode deslizar a janela
            if (this.messageState[i]) {
                maxPos = i;
            } else {
                break;
            }
        }
        if (maxPos == -1) {
            return;
        }
        // Desliza a janela
        for (int i = 0; i < maxPos + 1; i++) {
            this.messageList[i] = null;
            this.messageState[i] = false;
            this.timeoutCounter[i] = 0;
        }
        for (int i = maxPos + 1; i < this.windowSize; i++) {
            this.messageList[i - maxPos - 1] = this.messageList[i];
            this.messageState[i - maxPos - 1] = this.messageState[i];
            this.timeoutCounter[i - maxPos - 1] = this.timeoutCounter[i];
            this.messageList[i] = null;
            this.messageState[i] = false;
            this.timeoutCounter[i] = 0;
        }
        // Verifica a nova primeira mensagem
        if (!this.isEmpty()) {
            this.firstMessage = (int) this.messageList[0].getSeqNumber();
        }
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
        return messageList[0] == null;
    }

    public synchronized MessagePacket verifyTimeout(long timeout) {
        timeout = timeout * 1000; // Timeout é passado em segundos
        for (int i = 0; i < this.windowSize; i++) {
            if (this.messageList[i] != null && this.messageState[i] == false) {
                if (System.currentTimeMillis() - this.timeoutCounter[i] > timeout) { // Se estorou o timeout, retorna a mensagem (Considera uma mensagem de cada vez)
                    this.timeoutCounter[i] = System.currentTimeMillis();
                    return this.messageList[i];
                }
            }
        }
        return null;
    }
}
