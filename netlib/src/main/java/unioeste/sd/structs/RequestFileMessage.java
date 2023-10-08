package unioeste.sd.structs;

public class RequestFileMessage extends Message{
    public String filename;

    public RequestFileMessage(User user, String filename) {
        this.user = user;
        this.filename = filename;
    }
}
