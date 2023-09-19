package unioeste.sd;

import unioeste.sd.structs.*;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client implements Runnable{
    private Connection connection;
    private List<User> chatUsers;
    private boolean isRunning = false;

    public boolean tryInitConnection(String ip, int port, User user) {
        Socket socket = null;
        try {
            socket = new Socket(ip,port);

            connection = new Connection(socket);
            connection.user = user;

            connection.sendMessage(new ClientInfoMessage(user));
            ClientsListMessage listMsg = connection.readMessage();
            chatUsers = listMsg.users;

            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void run() {
        isRunning = true;
        try {
            while (isRunning) {
                Message msg;
                msg = connection.readMessage();
                if (msg instanceof ChatMessage) {
                    ChatMessage chatmessage = (ChatMessage) msg;
                    System.out.println("[" + chatmessage.user.username + "]: " + chatmessage.text);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
