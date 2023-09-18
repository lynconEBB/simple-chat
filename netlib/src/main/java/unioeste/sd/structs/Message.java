package unioeste.sd.structs;

import java.io.Serializable;

public abstract class Message implements Serializable {
    public User user;

    public Message() { }

    public Message(User user) {
        this.user = user;
    }
}
