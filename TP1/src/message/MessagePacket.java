package message;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class MessagePacket {

    private long seqNumber;                 // número de sequencia da mensagem (unsigned 64 bits )
    private Instant time;                   // Time Stamp da mensagem
    private String message;                 // a string da mensagem (lenght < 2^14 bytes)
    private byte[] md5;                     // codigo de correção de erro (128 bits)

    public static final long MAX_MESSAGE_SIZE = 32768;

    MessagePacket(long seqNumber, String message) {
        if (message.length() > MAX_MESSAGE_SIZE) { // Verifica se tamanho da mensagem é aceitável
            throw new IllegalArgumentException("Mensagem muito grande");
        }
        this.seqNumber = seqNumber;
        this.message = message; // Define o conteúdo da mensagem
        time = Instant.now(); // Cria o timestamp da mensagem
    }

    /**
     * Constrói a mensagem em bytes para ser enviada na rede.
     *
     * @return a mensagem em bytes.
     * @throws NoSuchAlgorithmException
     */
    public byte[] buildMessageBytes() throws NoSuchAlgorithmException {
        byte[] messageWithoutMD5 = buildMessageWithoutMD5InBytes();
        byte[] messageMD5 = computeMessageMD5(messageWithoutMD5);

        ByteBuffer messageBuffer = ByteBuffer.allocate(messageWithoutMD5.length + messageMD5.length);
        messageBuffer.put(messageWithoutMD5);
        messageBuffer.put(messageMD5);
        return messageBuffer.array();

    }

    /**
     * Constrói a mensagem em Bytes. Apenas inclui o header e o conteúdo da
     * mensagem.
     *
     * @return a mesagem, sem o MD5, em bytes.
     */
    private byte[] buildMessageWithoutMD5InBytes() {
        int messageSize = 22 + this.message.length();
        ByteBuffer byteMessage = ByteBuffer.allocate(messageSize);
        byteMessage.putLong(seqNumber);
        byteMessage.putLong(time.getEpochSecond());
        byteMessage.putInt(time.getNano());
        byteMessage.putShort((short) this.message.length());
        byteMessage.put(this.message.getBytes(Charset.forName("US-ASCII")));
        return byteMessage.array();
    }

    /**
     * Calcula o código de verificação MD5 da mensagem em bytes.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    private byte[] computeMessageMD5(byte[] msg) throws NoSuchAlgorithmException {
        return java.security.MessageDigest.getInstance("MD5").digest(msg);
    }

}
