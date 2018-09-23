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
    public byte[] buildAckBytes(boolean keepAckMD5) throws NoSuchAlgorithmException {
        byte[] ackWithoutMD5 = buildAckWithoutMD5InBytes();
        byte[] ackMD5 = computeAckMD5(ackWithoutMD5);

        if (!keepAckMD5) {
            ackMD5 = messWithMd5(ackMD5);
        }

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

    public long getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(long seqNumber) {
        this.seqNumber = seqNumber;
    }

}
