package unioeste.sd;

import unioeste.sd.structs.*;

import java.io.IOException;

public class IncomingMessagesManager implements Runnable{
    private Client client;
    private MainWindow mainWindow;

    public IncomingMessagesManager(Client client, MainWindow mainWindow) {
        this.client = client;
        this.mainWindow = mainWindow;
    }

    @Override
    public void run() {
        try {
            while (client.isRunning()) {
                Message msg = client.getConnection().readMessage();

                if (msg instanceof ChatMessage) {
                    mainWindow.handleNewChatMessage((ChatMessage) msg);
                }
                else if (msg instanceof ClientsListMessage) {
                    mainWindow.handleNewClientsListMessage((ClientsListMessage) msg);
                }
                else if (msg instanceof FilePacketMessage) {
                    mainWindow.handleFilePacketMessage((FilePacketMessage) msg);
                }
                else if (msg instanceof AvailableFileMessage) {
                    mainWindow.handleAvailableFileMessage((AvailableFileMessage) msg);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            client.setRunning(false);
            throw new RuntimeException(e);
        }
    }
}
