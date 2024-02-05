package unioeste.sd;

import unioeste.sd.structs.FilePacketMessage;
import unioeste.sd.structs.Message;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.*;

public class OutgoingMessagesManager implements Runnable{
    private Client client;
    private MainWindow mainWindow;
    private Queue<Message> messageQueue;
    private Queue<Message> fileMessages;

    public OutgoingMessagesManager(Client client, MainWindow mainWindow) {
        this.client = client;
        this.mainWindow = mainWindow;
        this.fileMessages = new LinkedBlockingQueue<>();
        this.messageQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        try {
            while (client.isRunning()) {
                if (!fileMessages.isEmpty()) {
                    Message msg = fileMessages.remove();
                    client.getConnection().sendMessage(msg);
                }
                if (!messageQueue.isEmpty()) {
                    Message msg = messageQueue.remove();
                    client.getConnection().sendMessage(msg);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Message message) {
        if (message instanceof FilePacketMessage) {
            fileMessages.add(message);
        } else {
            messageQueue.add(message);
        }
    }
}
