package unioeste.sd.connection;

import unioeste.sd.structs.Message;
import unioeste.sd.structs.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class Connection {

    public User user;

    abstract public <T extends Message> T  readMessage() throws IOException, ClassNotFoundException;

    abstract public void sendMessage(Message message) throws IOException;

    abstract public void close() throws IOException;

    abstract public boolean isConnected();
    public void addMessage(Message message) {

    }
}