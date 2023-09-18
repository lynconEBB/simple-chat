package unioeste.sd.structs;

import java.util.List;

public class ClientsListMessage extends Message{
    public List<User> users;

    public ClientsListMessage(User user, List<User> users) {
        super(user);
        this.users = users;
    }
}
