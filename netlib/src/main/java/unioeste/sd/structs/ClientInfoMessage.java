package unioeste.sd.structs;

public class ClientInfoMessage extends Message {
    public User userInfo;

    public ClientInfoMessage(User userInfo) {
        this.user = userInfo;
        this.userInfo = userInfo;
    }
}
