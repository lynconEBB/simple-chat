package unioeste.sd;

import unioeste.sd.structs.*;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
    private static final String DEFAULT_SERVER_USERNAME = "SERVER";
    private User serverUser = new User(DEFAULT_SERVER_USERNAME);
    private final int port = 54000;
    private boolean isRunning = false;
    private Map<User, Connection> connections = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Override
    public void run() {
        isRunning = true;
        try {
            ServerSocket listenSocket = new ServerSocket(port);
            printInfo();

            while (isRunning) {
                Socket newSocket = listenSocket.accept();
                System.out.println("Novo cliente aceito");
                HandleClientTask newTask = new HandleClientTask(newSocket,this);
                executorService.execute(newTask);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printInfo() {
        try {
            System.out.println("Server Initiated");
            System.out.println("Server ip: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Server port:" + port);
        } catch (UnknownHostException e) {
            System.out.println("Cant get server ip!");
            throw new RuntimeException(e);
        }
    }

    public void sendToAllInclusive(User sourceUser, MessageType type) {
        sendToAll(sourceUser,type, true);
    }

    public void sendToAll(User sourceUser, MessageType type) {
        sendToAll(sourceUser,type, false);
    }

    public void sendToAll(User sourceUser, Message message) {
        sendToAll(sourceUser,message, false);
    }

    private void sendToAll(User sourceUser, MessageType type, boolean isInclusive) {
        Message messageToBeSent = switch (type) {
            case CLIENTS_LIST_UPDATE -> new ClientsListMessage(sourceUser, connections.keySet().stream().toList());
        };

        sendToAll(sourceUser, messageToBeSent, isInclusive);
    }

    private void sendToAll(User sourceUser, Message message, boolean isInclusive) {
        message.user = sourceUser;

        connections.forEach((user, connection) -> {
            if (isInclusive || user != sourceUser) {
                try {
                    connection.sendMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public Map<User, Connection> getConnections() {
        return connections;
    }

    public User getServerUser() {
        return serverUser;
    }

    public void parseCommand(User user, String text) {
        text = text.substring(1);
        String[] parts = text.split(" ");

        if (parts.length == 0)
            return;

        if (parts[0] == "kick") {

        }
    }
}