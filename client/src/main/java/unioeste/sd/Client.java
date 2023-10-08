package unioeste.sd;

import unioeste.sd.connection.Connection;
import unioeste.sd.connection.TcpConnection;
import unioeste.sd.connection.UdpConnection;
import unioeste.sd.structs.*;

import java.io.*;
import java.net.*;

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
    private Main mainWindow;
    private volatile boolean isRunning = false;

    public IncomingMessagesManager inManager;
    public OutgoingMessagesManager outManager;

    public Client(Main mainWindow) {
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
                System.out.println("packet sent");

                byte[] in = new byte[64000];
                DatagramPacket receivPacket = new DatagramPacket(in, in.length);
                socket.receive(receivPacket);
                ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(receivPacket.getData()));
                Message firstMessage = (Message) inStream.readObject();

                System.out.println("packet received");

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
}
