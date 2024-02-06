package unioeste.sd.structs;

public class WrongCredentialsMessage extends Message {
    public User wrongUser;

    public WrongCredentialsMessage(User srcUser, User wrongUser) {
        this.user = srcUser;
        this.wrongUser = wrongUser;
    }
}
