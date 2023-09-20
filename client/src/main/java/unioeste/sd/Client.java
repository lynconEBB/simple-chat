package unioeste.sd;

import unioeste.sd.structs.*;
import unioeste.sd.widgets.MessageWidget;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
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

    public boolean tryInitConnection(String ip, int port, User user) {
        Socket socket;
        try {
            socket = new Socket(ip,port);

            connection = new Connection(socket);
            connection.user = user;

            connection.sendMessage(new ClientInfoMessage(user));

            ClientsListMessage listMsg = connection.readMessage();
            mainWindow.handleNewClientsListMessage(listMsg);

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
