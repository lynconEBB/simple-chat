package unioeste.sd.structs;

public class AvailableFileMessage extends Message{
    public String filename;

    public AvailableFileMessage(User user, String filename) {
        this.user = user;
        this.filename = filename;
    }
}
