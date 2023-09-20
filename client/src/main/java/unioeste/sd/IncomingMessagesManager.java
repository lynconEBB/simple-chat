package unioeste.sd;

import unioeste.sd.structs.ChatMessage;
import unioeste.sd.structs.ClientsListMessage;
import unioeste.sd.structs.Connection;
import unioeste.sd.structs.Message;

import java.io.IOException;

public class IncomingMessagesManager implements Runnable{
    private Client client;
    private Main mainWindow;

    public IncomingMessagesManager(Client client, Main mainWindow) {
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

            }
        } catch (IOException | ClassNotFoundException e) {
            client.setRunning(false);
            throw new RuntimeException(e);
        }
    }
}
