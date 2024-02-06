package unioeste.sd;


import unioeste.sd.connection.Connection;
import unioeste.sd.connection.TcpConnection;
import unioeste.sd.connection.UdpConnection;
import unioeste.sd.structs.*;

import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class HandleClientTask implements Runnable{
    private final Server server;
    private final Connection connection;
    private final OutgoingMessageManager outManager;

    // Used for TCP connections
    public HandleClientTask(Socket socket, Server server) throws IOException {
        this.connection = new TcpConnection(socket);
        this.outManager = new OutgoingMessageManager(connection);
        this.server = server;
    }

    // Used for UDP connections
    public HandleClientTask(SocketAddress socketAddress, Server server, ClientInfoMessage infoMessage, DatagramSocket socket) throws IOException {
        this.connection = new UdpConnection(socketAddress, socket);
        this.connection.addMessage(infoMessage);
        this.outManager = new OutgoingMessageManager(connection);
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("[DEBUG]> Starting handle client task!");

        try {
            ClientInfoMessage clientMsg = connection.readMessage();

            if (clientMsg == null)
                throw new RuntimeException("Mensagem inválida");

            if (server.getConnections().containsKey(clientMsg.userInfo)) {
                throw new RuntimeException("Usuário com nome ja existente");
            }

            connection.user = clientMsg.userInfo;
            server.getConnections().put(clientMsg.userInfo, connection);

            server.getOutManagers().put(clientMsg.userInfo, outManager);
            outManager.start();

            server.sendToAll(server.getServerUser(), MessageType.CLIENTS_LIST_UPDATE);
            server.sendToAll(server.getServerUser(), new ChatMessage("Usuario " + connection.user.username + " entrou no chat!"));
            System.out.println("Usuário " + connection.user.username + " entrou no chat!");

            loop();
        } catch (IOException | ClassNotFoundException e) {
            this.gracefulShutdown();
            throw new RuntimeException(e);
        }
    }

    private void loop() throws IOException, ClassNotFoundException {
        while (connection.isConnected()) {
            Message msg = connection.readMessage();

            if (msg instanceof ChatMessage chatMessage) {
                if (chatMessage.text.charAt(0) == '/') {
                    server.parseCommand(connection.user, chatMessage.text);
                } else {
                    server.sendToAll(connection.user, chatMessage);
                }
            }
            if (msg instanceof FilePacketMessage filePacket) {
                server.handleFilePacket(filePacket);
            }
            if (msg instanceof RequestFileMessage requestMessage) {
                System.out.println("Request message");
                server.handleFileRequest(requestMessage);
            }
            if (msg instanceof CloseMessage) {
                gracefulShutdown();
            }
            if (msg instanceof ClientInfoMessage infoMessage) {
                server.handleClientUpdateMessage(infoMessage);
            }
            if (msg instanceof WrongCredentialsMessage wrongMessage) {
               server.handleWrongCredentialsMessage(wrongMessage);
            }
        }
    }

    private void gracefulShutdown() {
        try {
            connection.close();
        } catch (IOException ignored) { }

        if (server.getConnections().containsKey(connection.user)) {
            server.getConnections().remove(connection.user);
            server.sendToAll(server.getServerUser(), MessageType.CLIENTS_LIST_UPDATE);
            server.sendToAll(server.getServerUser(), new ChatMessage("Usuario " + connection.user.username + " saiu do chat!"));
        }
        server.getOutManagers().remove(connection.user);
    }
}
