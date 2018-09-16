package window;

import message.MessagePacket;

public class SlidingWindow{
    private MessagePacket[] messageList;        // lista de mensagens na janela
    private int windowSize;                     // tamanho da janela
    private long firstMessage;

    public SlidingWindow(int windowSize){
        this.windowSize = windowSize;
        this.messageList = new MessagePacket[this.windowSize];
    }

    //TODO
}