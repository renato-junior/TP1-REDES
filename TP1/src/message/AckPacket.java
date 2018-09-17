package message;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

/**
 *
 * @author renatojuniortmp
 */
public class AckPacket {

    private long seqNumber;
    private Instant time;
    private byte[] md5;

    public AckPacket(long seqNumber) {
        this.seqNumber = seqNumber;
        time = Instant.now(); // Cria o timestamp da mensagem
    }

    public AckPacket(byte[] packet) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(packet);
        this.seqNumber = byteBuffer.getLong();
        time = Instant.ofEpochSecond(byteBuffer.getLong(), byteBuffer.getInt());
        md5 = new byte[16];
        byteBuffer.get(md5, 0, 16);
    }

    /**
     * Constrói o Ack em bytes para ser enviada na rede.
     *
     * @return o Ack em bytes.
     * @throws NoSuchAlgorithmException
     */
    public byte[] buildAckBytes() throws NoSuchAlgorithmException {
        byte[] ackWithoutMD5 = buildAckWithoutMD5InBytes();
        byte[] ackMD5 = computeAckMD5(ackWithoutMD5);

        ByteBuffer messageBuffer = ByteBuffer.allocate(ackWithoutMD5.length + ackMD5.length);
        messageBuffer.put(ackWithoutMD5);
        messageBuffer.put(ackMD5);
        return messageBuffer.array();

    }

    /**
     * Constrói o Ack em Bytes. Apenas inclui os campos do Ack.
     *
     * @return o Ack, sem o MD5, em bytes.
     */
    private byte[] buildAckWithoutMD5InBytes() {
        int messageSize = 20;
        ByteBuffer byteMessage = ByteBuffer.allocate(messageSize);
        byteMessage.putLong(this.getSeqNumber());
        byteMessage.putLong(time.getEpochSecond());
        byteMessage.putInt(time.getNano());
        return byteMessage.array();
    }

    /**
     * Calcula o código de verificação MD5 do ack em bytes.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    private byte[] computeAckMD5(byte[] msg) throws NoSuchAlgorithmException {
        return java.security.MessageDigest.getInstance("MD5").digest(msg);
    }

    public boolean checkAckMD5() throws NoSuchAlgorithmException {
        if (java.util.Arrays.equals(md5, computeAckMD5(buildAckWithoutMD5InBytes()))) {
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

}
