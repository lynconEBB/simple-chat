package unioeste.sd;

import unioeste.sd.structs.*;
import unioeste.sd.widgets.MessageWidget;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable{
    private Connection connection;
    private Main mainWindow;
    private boolean isRunning = false;

    public IncomingMessagesManager inManager;
    public OutgoingMessagesManager outManager;

    public Client(Main mainWindow) {
        this.mainWindow = mainWindow;
        this.inManager = new IncomingMessagesManager(this, mainWindow);
        this.outManager = new OutgoingMessagesManager(this, mainWindow);
    }

    public boolean tryInitConnection(String ip, int port, User user) {
        Socket socket = null;
        try {
            socket = new Socket(ip,port);

            connection = new Connection(socket);
            connection.user = user;

            connection.sendMessage(new ClientInfoMessage(user));

            ClientsListMessage listMsg = connection.readMessage();
            mainWindow.handleNewClientsListMessage(listMsg);

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

    @Override
    public void run() {
        isRunning = true;
        try {
            while (isRunning) {
                Message msg = connection.readMessage();

                if (msg instanceof ChatMessage) {
                    mainWindow.handleNewChatMessage((ChatMessage) msg);
                }
                else if (msg instanceof ClientsListMessage) {
                    mainWindow.handleNewClientsListMessage((ClientsListMessage) msg);
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            this.isRunning = false;
            throw new RuntimeException(e);
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
