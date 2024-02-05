package unioeste.sd;

import imgui.type.ImString;
import org.apache.commons.lang3.StringUtils;
import unioeste.sd.connection.Connection;
import unioeste.sd.connection.TcpConnection;
import unioeste.sd.connection.UdpConnection;
import unioeste.sd.structs.*;

import javax.crypto.*;
import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Client {


    static class ShutdownHookTask extends Thread {
        private final Client client;
        ShutdownHookTask(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                client.getConnection().sendMessage(new CloseMessage());
            } catch (IOException ignored) { }

            client.setRunning(false);
        }
    }

    private Connection connection;
    private MainWindow mainWindow;
    private volatile boolean isRunning = false;

    public IncomingMessagesManager inManager;
    public OutgoingMessagesManager outManager;

    public Client(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.inManager = new IncomingMessagesManager(this, mainWindow);
        this.outManager = new OutgoingMessagesManager(this, mainWindow);
    }

    public boolean initConnection(String ip, int port, User user, boolean useTCP) {
        try {
            if (useTCP) {
                Socket socket = new Socket(ip,port);

                connection = new TcpConnection(socket);
                connection.user = user;

                connection.sendMessage(new ClientInfoMessage(user));

                ClientsListMessage listMsg = connection.readMessage();
                mainWindow.handleNewClientsListMessage(listMsg);

            } else {
                System.out.println("Initing udp connection");
                DatagramSocket socket = new DatagramSocket();

                SocketAddress serverAddress = new InetSocketAddress(ip, port);
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
                objectStream.writeObject(new ClientInfoMessage(user));
                objectStream.close();

                DatagramPacket packet = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length, serverAddress);
                socket.send(packet);

                byte[] in = new byte[64000];
                DatagramPacket receivPacket = new DatagramPacket(in, in.length);
                socket.receive(receivPacket);
                ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(receivPacket.getData()));
                Message firstMessage = (Message) inStream.readObject();

                connection = new UdpConnection(receivPacket.getSocketAddress(), socket);
                connection.user = user;
                connection.addMessage(firstMessage);

                Runtime.getRuntime().addShutdownHook(new ShutdownHookTask(this));
            }

            isRunning = true;

            Thread inManagerThread = new Thread(inManager);
            inManagerThread.setDaemon(true);
            inManagerThread.start();

            Thread outManagerThread = new Thread(outManager);
            outManagerThread.setDaemon(true);
            outManagerThread.start();

            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public Connection getConnection() {
        return connection;
    }

    public ChatMessage createMessageFromString(String s) {
        s = s.trim();
        if (s.charAt(0) != '/') {
            return new ChatMessage(s, connection.user);
        }
        if (StringUtils.countMatches(s, " ") < 2) {
            mainWindow.handleNewChatMessage(new ChatMessage("Command Incorrect!", new User("SERVER")));
            return null;
        }

        int firstSpaceIndex = StringUtils.ordinalIndexOf(s, " ", 1);
        int secondSpaceIndex = StringUtils.ordinalIndexOf(s, " ", 2);
        String dstUsername = s.substring(firstSpaceIndex + 1, secondSpaceIndex);
        String firstPart = s.substring(0, secondSpaceIndex + 1);
        String msgTxt = s.substring(secondSpaceIndex + 1);

        SecretKey secretKey = mainWindow.secretDialog.getSecretKeyByUser(new User(dstUsername));

        if (secretKey == null) {
            ChatMessage chatMessage = new ChatMessage(s, connection.user);
            chatMessage.isWhisper = true;

            return chatMessage;
        }

        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(msgTxt.getBytes());
            String encryptedMessage = Base64.getEncoder().encodeToString(bytes);

            ChatMessage chatMessage = new ChatMessage(firstPart + encryptedMessage, connection.user);
            chatMessage.isWhisper = true;

            return chatMessage;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseCommand(User user, String text) throws IOException {
    }
}
