package unioeste.sd.connection;

import unioeste.sd.structs.Message;
import unioeste.sd.structs.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
    private final Socket socket;
    private final ObjectOutputStream objOutStream;
    private final ObjectInputStream objInStream;

    public User user;

    public Connection(Socket socket, User user) throws IOException {
        this.user = user;
        this.socket = socket;
        objOutStream = new ObjectOutputStream(socket.getOutputStream());
        objInStream = new ObjectInputStream(socket.getInputStream());
    }

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        objOutStream = new ObjectOutputStream(socket.getOutputStream());
        objInStream = new ObjectInputStream(socket.getInputStream());
    }

    public <T extends Message> T  readMessage() throws IOException, ClassNotFoundException {
        return (T) objInStream.readObject();
    }

    public void sendMessage(Message message) throws IOException {
        objOutStream.writeObject(message);
    }

    public void close() throws IOException {
        socket.close();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}