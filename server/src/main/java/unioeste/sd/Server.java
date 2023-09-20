package unioeste.sd;

import unioeste.sd.connection.Connection;
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
    private Map<User, OutgoinMessageManager> outManagers = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void start() {
        new Thread(new Server()).start();
    }
    @Override
    public void run() {
        isRunning = true;
        try {
            ServerSocket listenSocket = new ServerSocket(port);
            printInfo();

            while (isRunning) {
                Socket newSocket = listenSocket.accept();
                System.out.println("[DEBUG]> Novo cliente aceito!");

                HandleClientTask newTask = new HandleClientTask(newSocket,this);
                executorService.execute(newTask);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTo(User sourceUser, User dstUser,  ChatMessage message ) throws IOException {
        message.user = sourceUser;
        message.isWhisper = true;
        if (connections.containsKey(dstUser)) {
            connections.get(dstUser).sendMessage(message);
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

        outManagers.forEach((user, outManager) -> {
            if (isInclusive || user != sourceUser) {
                outManager.sendMessage(message);
            }
        });
    }

    public void parseCommand(User user, String text) throws IOException {
        text = text.substring(1);
        String[] parts = text.split(" ");

        if (parts.length == 0)
            return;

        if (parts[0].equals("whisper") && parts.length == 3) {
            Connection dstUserConnection = connections.get(new User(parts[1]));
            if (dstUserConnection != null) {
                ChatMessage msg = new ChatMessage(parts[2]);
                msg.isWhisper = true;
                msg.user = user;
                dstUserConnection.sendMessage(msg);
            } else {
                sendTo(serverUser, user, new ChatMessage("User not found!"));
            }
        } else {
            sendTo(serverUser, user, new ChatMessage("Command incorrect!"));
        }
    }

    public Map<User, Connection> getConnections() {
        return connections;
    }

    public User getServerUser() {
        return serverUser;
    }

    public Map<User, OutgoinMessageManager> getOutManagers() {
        return outManagers;
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
}
