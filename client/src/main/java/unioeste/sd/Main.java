package unioeste.sd;

import unioeste.sd.structs.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost",54000);
        User clientUser = new User("test1");
        clientUser.name = "OSORIO";

        Connection connection = new Connection(socket);
        connection.user = clientUser;

        connection.sendMessage(new ClientInfoMessage(clientUser));
        ClientsListMessage listMsg = connection.readMessage();

        while (true) {
            Message msg = connection.readMessage();
            if (msg instanceof ChatMessage) {
                ChatMessage chatmessage = (ChatMessage) msg;
                System.out.println("[" + chatmessage.user.username + "]: " + chatmessage.text);
            }
        }
    }
}