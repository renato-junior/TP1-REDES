package window;

import message.MessagePacket;

public class SlidingWindow {

    private MessagePacket[] messageList;        // lista de mensagens na janela
    private boolean[] messageState;             // lista de confirmação de mensagens
    private int windowSize;                     // tamanho da janela
    private int firstMessage;                  // seqNumber na primeira mensagem na janela

    public SlidingWindow(int windowSize) {
        this.windowSize = windowSize;
        this.firstMessage = 0;

        // Inicializa vetores
        this.messageList = new MessagePacket[this.windowSize];
        this.messageState = new boolean[this.windowSize];
        for (int i = 0; i < this.windowSize; i++) {
            this.messageList[i] = null;
            this.messageState[i] = false;
        }
    }

    /**
     * Adiciona a mensagem na janela.
     *
     * @param mp a mensagem a ser adicionada.
     * @throws IllegalArgumentException
     */
    public void addMessage(MessagePacket mp) {
        if(this.isEmpty()){
            this.firstMessage = (int) mp.getSeqNumber();
        }
        int pos = (int) mp.getSeqNumber() - this.firstMessage;
        if(pos >= this.windowSize) {
            throw new IllegalArgumentException("Impossível adicionar mensagem na janela");
        }
        this.messageList[pos] = mp;
        this.messageState[pos] = false;
    }
    
    /**
     * Confirma a mensagem com o seqNum.
     * @param msgSeqNum o seqNum da mensagem a ser confirmada.
     */
    public void confirmMessage(long msgSeqNum){
        int pos = (int) msgSeqNum - this.firstMessage;
        this.messageState[pos] = true;
    }
    
    /**
     * Desliza a janela deslizante, tirando todos os primeiros pacotes que foram confirmado.
     */
    public void slideWindow(){
        int maxPos = 0;
        for (int i = 0; i < this.windowSize; i++) { // Verifica até onde pode deslizar a janela
            if(this.messageState[i]){
                maxPos = i;
            } else {
                break;
            }
        }
        // Desliza a janela
        for (int i = maxPos+1; i < this.windowSize; i++) {
            this.messageList[i-maxPos-1] = this.messageList[i];
            this.messageState[i-maxPos-1] = this.messageState[i];
            this.messageList[i] = null;
            this.messageState[i] = false;
        }
        // Verifica a nova primeira mensagem
        if(!this.isEmpty()){
            this.firstMessage = (int) this.messageList[0].getSeqNumber();
        }
    }

    /**
     * Verifica se a janela está cheia.
     *
     * @return true se a janela está cheia. False caso contrário.
     */
    public boolean isFull() {
        return messageList[windowSize - 1] != null;
    }

    /**
     * Verifica se a janela está vazia.
     *
     * @return true se a janela está vazia. False caso contrário.
     */
    public boolean isEmpty() {
        return messageList[0] == null;
    }
}
