package message;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

/**
 *
 * @author renato
 */
public class MessagePacket {

    private long seqNumber;                 // número de sequencia da mensagem (unsigned 64 bits )
    private Instant time;                   // Time Stamp da mensagem
    private String message;                 // a string da mensagem (lenght < 2^14 bytes)
    private byte[] md5;

    /**
     *
     */
    public static final long MAX_MESSAGE_SIZE = 32768;

    public MessagePacket(long seqNumber, String message) {
        if (message.length() > MAX_MESSAGE_SIZE) { // Verifica se tamanho da mensagem é aceitável
            throw new IllegalArgumentException("Mensagem muito grande");
        }
        this.seqNumber = seqNumber;
        this.message = message; // Define o conteúdo da mensagem
        time = Instant.now(); // Cria o timestamp da mensagem
    }

    public MessagePacket(byte[] messageInBytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(messageInBytes);
        this.seqNumber = byteBuffer.getLong();
        this.time = Instant.ofEpochSecond(byteBuffer.getLong(), byteBuffer.getInt());
        short messageSize = byteBuffer.getShort();
        byte[] messageContentBytes = new byte[messageSize];
        byteBuffer.get(messageContentBytes, 0, messageSize);
        this.message = new String(messageContentBytes, Charset.forName("US-ASCII"));
        this.md5 = new byte[16];
        byteBuffer.get(this.md5, 0, 16);
    }

    /**
     * Constrói a mensagem em bytes para ser enviada na rede.
     *
     * @param keepMessageMD5 indica se o MD5 da mensagem será mantido ou
     * corrompido propositalmente.
     * @return a mensagem em bytes.
     * @throws NoSuchAlgorithmException
     */
    public byte[] buildMessageBytes(boolean keepMessageMD5) throws NoSuchAlgorithmException {
        byte[] messageWithoutMD5 = buildMessageWithoutMD5InBytes();
        byte[] messageMD5 = computeMessageMD5(messageWithoutMD5);

        if (!keepMessageMD5) {
            messageMD5 = messWithMd5(messageMD5);
        }

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
        int messageSize = 22 + this.getMessage().length();
        ByteBuffer byteMessage = ByteBuffer.allocate(messageSize);
        byteMessage.putLong(getSeqNumber());
        byteMessage.putLong(time.getEpochSecond());
        byteMessage.putInt(time.getNano());
        byteMessage.putShort((short) this.getMessage().length());
        byteMessage.put(this.getMessage().getBytes(Charset.forName("US-ASCII")));
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

    /**
     * Altera o MD5 para deixá-lo errado propositalmente.
     *
     * @param md5 o md5 a ser alterado.
     * @return o md5 alterado.
     */
    public byte[] messWithMd5(byte[] md5) {
        byte[] modifiedMd5 = new byte[md5.length];
        for (int i = 0; i < md5.length; i++) {
            modifiedMd5[i] = (byte) 255;
        }
        return modifiedMd5;
    }

    public boolean checkMessageMD5() throws NoSuchAlgorithmException {
        if (java.util.Arrays.equals(md5, computeMessageMD5(buildMessageWithoutMD5InBytes()))) {
            return true;
        } else {
            return false;
        }
    }

    public long getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(long seqNumber) {
        this.seqNumber = seqNumber;
    }

    public String getMessage() {
        return message;
    }

}
