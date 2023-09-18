package unioeste.sd;


import unioeste.sd.structs.*;

import java.io.*;
import java.net.Socket;

public class HandleClientTask implements Runnable{
    private final Server server;
    private final Connection connection;

    public HandleClientTask(Socket socket, Server server) throws IOException {
        this.connection = new Connection(socket);
        this.server = server;
    }

    @Override
    public void run() {
        try {
            ClientInfoMessage clientMsg = connection.readMessage();

            if (clientMsg == null)
                throw new RuntimeException("Mensagem inválida");

            if (server.getConnections().containsKey(clientMsg.userInfo)) {
                throw new RuntimeException("Usuário com nome já existente");
            }

            connection.user = clientMsg.userInfo;
            server.getConnections().put(clientMsg.userInfo, connection);

            server.sendToAll(server.getServerUser(), MessageType.CLIENTS_LIST_UPDATE);
            server.sendToAll(server.getServerUser(), new ChatMessage("Usuário " + connection.user.username + "entrou no chat!"));
            System.out.println("Usuário " + connection.user.username + " entrou no chat!");

            loop();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loop() throws IOException, ClassNotFoundException {
        while (connection.isConnected()) {
            Message msg = connection.readMessage();

            if (msg instanceof ChatMessage) {
                ChatMessage chatMessage = (ChatMessage) msg;
                if (chatMessage.text.charAt(0) == '/') {
                    server.parseCommand(connection.user, chatMessage.text);
                } else {
                    server.sendToAll(connection.user, chatMessage);
                }
            }
        }
    }
}
