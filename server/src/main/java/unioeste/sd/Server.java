package unioeste.sd;

import unioeste.sd.connection.Connection;
import unioeste.sd.structs.*;

import javax.crypto.*;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
    private static final String DEFAULT_SERVER_USERNAME = "SERVER";
    private User serverUser = new User(DEFAULT_SERVER_USERNAME);

    private FileManager fileManager;
    private final int port = 54000;
    private boolean isRunning = false;
    private boolean useTCP;
    private Map<User, Connection> connections = new ConcurrentHashMap<>();
    private Map<User, OutgoingMessageManager> outManagers = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public Server(boolean useTCP) {
        this.useTCP = useTCP;
        this.fileManager = FileManager.start(this);
    }

    public static void start(boolean useTCP) {
        new Thread(new Server(useTCP)).start();
    }

    @Override
    public void run() {
        isRunning = true;
        try {
            if (!useTCP) {
                DatagramSocket datagramSocket = new DatagramSocket(port);
                printInfo();

                while (isRunning) {
                    byte[] in = new byte[64000];
                    DatagramPacket packet = new DatagramPacket(in, in.length);
                    datagramSocket.receive(packet);

                    ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
                    Message genMessage = (Message) inStream.readObject();

                    if (genMessage instanceof ClientInfoMessage) {
                        DatagramSocket newSocket = new DatagramSocket();
                        HandleClientTask newTask = new HandleClientTask(packet.getSocketAddress(),this, (ClientInfoMessage) genMessage, newSocket);
                        executorService.execute(newTask);

                        System.out.println("[DEBUG]> Novo cliente aceito! Bind na porta: " + newSocket.getLocalPort());
                    }
                }
            } else {
                ServerSocket listenSocket = new ServerSocket(port);
                printInfo();

                while (isRunning) {
                    Socket newSocket = listenSocket.accept();
                    System.out.println("[DEBUG]> Novo cliente aceito!");

                    HandleClientTask newTask = new HandleClientTask(newSocket,this);
                    executorService.execute(newTask);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTo(User sourceUser, User dstUser, ChatMessage message ) throws IOException {
        message.user = sourceUser;
        message.isWhisper = true;
        if (connections.containsKey(dstUser)) {
            connections.get(dstUser).sendMessage(message);
        }
    }

    public void sendToAll(User sourceUser, MessageType type) {
        sendToAll(sourceUser,type, false);
    }

    public void sendToAll(User sourceUser, Message message) {
        sendToAll(sourceUser, message, false);
    }

    private void sendToAll(User sourceUser, MessageType type, boolean isInclusive) {
        switch (type) {
            case CLIENTS_LIST_UPDATE -> {
                outManagers.forEach(((user, outManager) -> {
                    List<User> usersInfo = connections.keySet().stream().toList();
                    for (User u : usersInfo) {
                        if (u != user)
                            u.key = null;
                    }
                    outManager.sendMessage(new ClientsListMessage(sourceUser, usersInfo));
                }));
            }
        };
    }

    private void sendToAll(User sourceUser, Message message, boolean isInclusive) {
        message.user = sourceUser;

        outManagers.forEach((user, outManager) -> {
            if (isInclusive || user != sourceUser) {
                outManager.sendMessage(message);
            }
        });
    }


    public Map<User, Connection> getConnections() {
        return connections;
    }

    public User getServerUser() {
        return serverUser;
    }

    public Map<User, OutgoingMessageManager> getOutManagers() {
        return outManagers;
    }

    public void parseCommand(User user, String text) throws IOException {
        String command = text.substring(0, text.indexOf(' '));
        text = text.substring(text.indexOf(' ') + 1);

        if (command.equalsIgnoreCase("/whisper")) {
            String destUsername = text.substring(0, text.indexOf(' '));
            text = text.substring(text.indexOf(' ') + 1);

            Connection dstUserConnection = connections.get(new User(destUsername));

            if (dstUserConnection != null) {
                if (dstUserConnection.user.key != null) {
                    try {
                        byte[] encrypted = Base64.getDecoder().decode(text);
                        Cipher cipher = Cipher.getInstance("DES");
                        cipher.init(Cipher.DECRYPT_MODE, dstUserConnection.user.key);
                        byte[] bytes = cipher.doFinal(encrypted);
                        text = new String(bytes);
                    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                             IllegalBlockSizeException | BadPaddingException | IllegalArgumentException e) {
                        sendTo(serverUser, user, new ChatMessage("Wrong key used, configure the user key in the secret button!"));
                        return;
                    }
                }
                ChatMessage msg = new ChatMessage(text);
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
    public void handleFilePacket(FilePacketMessage message) {
       fileManager.addMessage(message);
    }
    public void handleFileRequest(RequestFileMessage message) {
        fileManager.startFileSend(message);
    }
    public void handleClientUpdateMessage(ClientInfoMessage infoMessage) {
        if (infoMessage.user.equals(infoMessage.userInfo) && connections.containsKey(infoMessage.userInfo)) {
            Connection connection = connections.get(infoMessage.userInfo);
            connections.remove(infoMessage.user);
            connection.user = infoMessage.userInfo;
            connections.put(infoMessage.userInfo, connection);
        }
    }

    public void printInfo() {
        try {
            System.out.println("Server Initiated");
            System.out.println("Protocol: " + (useTCP ? "TCP" : "UDP"));
            System.out.println("Server ip: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Server port:" + port);
        } catch (UnknownHostException e) {
            System.out.println("Cant get server ip!");
            throw new RuntimeException(e);
        }
    }

}
