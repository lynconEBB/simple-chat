package unioeste.sd.structs;

public class FilePacketMessage extends Message{
    public String fileName;
    public byte[] bytes;
    public FilePacketMessage(User user, String fileName, byte[] bytes) {
        super(user);
        this.fileName = fileName;
        this.bytes = bytes;
    }
}
